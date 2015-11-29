package cn.yxffcode.easytookit.logqueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

/**
 * A file system log queue implementation which takes a line as a record.
 * <p>
 * Because of the I/O is stateful, the {@link #offer(Object)} method is a synchronized method, if synchronized method
 * affects the performance,io can be rewritten serialized using ExecutorService.
 * <p>
 * The {@link #offer(Object)} method is synchronized that it can be invoked concurrently,but the {@link #poll()} method
 * is not thread safe,it can only be invoked serialized.
 * <p>
 * When a thread write an object to the log file but another thread read the same file,the read thread main get a error
 * result,to resolve this problem is dependence,so I don't allow read and write a file at the same time.
 * <p>
 * It's better to invoke the {@link #rotate()} method before polling all remaining object.
 * <p>
 * deprecated by {@link FSLogQueue}
 *
 * @author gaohang on 15/9/11.
 */
@Deprecated
public class FSLogQueueOld<T> implements LogQueue<T> {

    private static final Logger LOGGER                = LoggerFactory.getLogger(FSLogQueueOld.class);
    private static final char   ENTITY_DELIMITER      = '\n';
    private static final char   LOG_FILE_ID_DELIMITER = '_';

    private final Codec<T>       codec;
    private       BufferedWriter out;
    private       BufferedReader in;
    private       int            writeIndex;
    private       int            readIndex;
    private       File           currentReadFile;
    private final String         dir;
    private final String         baseFilename;

    public FSLogQueueOld(Codec<T> codec, String dir, final String baseFilename) {
        this.codec = codec;
        this.dir = dir;
        this.baseFilename = baseFilename;
        File file = new File(dir);
        if (!file.exists()) {
            mkdir(file);
        }
        File[] files = file.listFiles();
        int    max   = -1;//min is 0
        int    min   = Integer.MAX_VALUE;
        if (isNotEmpty(files)) {
            for (File f : files) {
                String name = f.getName();
                int index = name.lastIndexOf(LOG_FILE_ID_DELIMITER);
                if (index < 0) {
                    continue;
                }
                try {
                    int i = Integer.parseInt(name.substring(index + 1));
                    max = Math.max(i, max);
                    min = Math.min(i, min);
                } catch (NumberFormatException e) {
                    LOGGER.error("illegal data in log file", e);
                }
            }
        }
        this.writeIndex = max;
        this.readIndex = min;
    }

    @Override
    public synchronized void rotate() throws RotateQueueException {
        BufferedWriter out = this.out;
        try {
            openNewFileToWrite();
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            LOGGER.error("close file error when rotate.writeIndex:{}", writeIndex);
        }
    }

    @Override
    public synchronized void offer(T obj) {
        if (obj == null) {
            throw new NullPointerException("offer a null object");
        }
        if (out == null) {
            try {
                openNewFileToWrite();
            } catch (RotateQueueException e) {
                LOGGER.error("open file error,writeIndex:{}", writeIndex);
            }
        }
        if (out == null) {
            throw new NullPointerException();
        }
        String value = codec.encode(obj);
        try {
            out.write(value + ENTITY_DELIMITER);
            //fixme:keep a buffer may have a better performance
            out.flush();
        } catch (IOException e) {
            throw new QueueOperationException(e);
        }
    }

    @Override
    public T poll() {
        if (readIndex > writeIndex) {
            return null;
        }
        if (currentReadFile == null) {
            openFileToRead();
        }
        if (currentReadFile == null) {
            return null;
        }
        String line;
        try {
            while ((line = in.readLine()) != null) {

                if (StringUtils.isBlank(line)) {
                    continue;
                }
                try {
                    T obj = codec.decode(line);
                    if (obj != null) {
                        return obj;
                    }
                } catch (Throwable throwable) {
                    //decode failed, format error
                    LOGGER.error("decode error, check format.line:{}", line, throwable);
                }
            }
            currentReadFile.delete();
        } catch (IOException e) {
            currentReadFile = null;
            closeReader();
            LOGGER.error("read error, {}", readIndex, e);
        }
        currentReadFile = null;
        //read next file
        return poll();
    }

    @Override
    public void close() throws IOException {
        for (Closeable closeable : Arrays.asList(out, in)) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    LOGGER.error("close error", e);
                }
            }
        }
    }

    private void mkdir(File file) {
        Stack<File> stack      = new Stack<File>();
        File        parentFile = file;
        while (parentFile != null && !parentFile.exists()) {
            stack.add(parentFile);
            parentFile = parentFile.getParentFile();
        }
        for (File f : stack) {
            f.mkdir();
        }
    }

    private <E> boolean isNotEmpty(E[] array) {
        return array != null && array.length != 0;
    }

    private void openNewFileToWrite() throws RotateQueueException {
        //writeIndex may be negative if the writeIndex = Integer.MAX_VALUE and execute writeIndex++,but makes no effort.
        writeIndex++;
        File file = new File(dir, nextWriteFile());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                writeIndex--;
                throw new RotateQueueException(e);
            }
        }
        try {
            this.out = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            writeIndex--;
            throw new RotateQueueException(e);
        }
    }

    private void openFileToRead() {
        BufferedReader in = this.in;
        if (this.readIndex >= writeIndex) {
            currentReadFile = null;
            return;
        }
        int readIndex = this.readIndex;
        try {
            currentReadFile = new File(dir, nextReadFile());
            if (currentReadFile.exists()) {
                try {
                    this.in = new BufferedReader(new FileReader(currentReadFile));
                    this.readIndex++;
                } catch (FileNotFoundException e) {
                    LOGGER.error("open error:{}", this.readIndex, e);
                    currentReadFile = null;
                    closeReader();
                    this.readIndex++;
                    //read next file
                    openFileToRead();
                }
            } else {
                openFileToRead();
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("close file error:{}", readIndex, e);
                }
            }
        }
    }

    private String nextWriteFile() {
        return nextFile(writeIndex);
    }

    private String nextReadFile() {
        return nextFile(readIndex);
    }

    private String nextFile(int index) {
        return new StringBuilder(baseFilename).append(LOG_FILE_ID_DELIMITER).append(index).toString();
    }

    private void closeReader() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error("close file error,{}", readIndex - 1, e);
            }
        }
    }

}
