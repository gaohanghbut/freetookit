package cn.yxffcode.easytookit.logqueue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * A file system log queue implementation which takes a line as a record.
 * <p/>
 * Because of the I/O is stateful, the {@link #offer(Object)} method is a synchronized method, if synchronized method
 * affects the performance,io can be rewritten serialized using ExecutorService.
 * <p/>
 * The {@link #offer(Object)} method is synchronized that it can be invoked concurrently,but the {@link #poll()} method
 * is not thread safe,it can only be invoked serialized.
 * <p/>
 * When a thread write an object to the log file but another thread read the same file,the read thread main get a error
 * result,to resolve this problem is dependence,so I don't allow read and write a file at the same time.
 * <p/>
 * It's better to invoke the {@link #rotate()} method before polling all remaining object.
 * <p/>
 *
 * @author gaohang on 15/9/11.
 */
public class FSLogQueue<T> implements LogQueue<T> {

    private static final char ENTITY_DELIMITER      = '\n';
    private static final char LOG_FILE_ID_DELIMITER = '_';

    private final Codec<T> codec;
    private final String   dir;
    private final String   baseFilename;
    private final LinkedList<File> logFiles = new LinkedList<>();
    private BufferedWriter out;
    private BufferedReader in;

    public FSLogQueue(Codec<T> codec,
                      String dir,
                      final String baseFilename
                     ) {
        this(codec, dir, baseFilename, new FilenameFilter() {
            @Override
            public boolean accept(File dir,
                                  String name
                                 ) {
                return true;
            }
        });
    }

    public FSLogQueue(Codec<T> codec,
                      String dir,
                      final String baseFilename,
                      FilenameFilter filter
                     ) {
        this.codec = codec;
        this.dir = dir;
        this.baseFilename = baseFilename;
        File file = new File(dir);
        if (! file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles(filter);
        if (isNotEmpty(files)) {
            for (File f : files) {
                String name = f.getName();
//                int index = name.lastIndexOf(LOG_FILE_ID_DELIMITER);
                if (! name.contains(baseFilename)) {
                    continue;
                }
                logFiles.add(f);
            }
        }
    }

    private <E> boolean isNotEmpty(E[] array) {
        return array != null && array.length != 0;
    }

    @Override
    public void close() throws IOException {
        closeReader();
        closeWriter();
    }    @Override
    public synchronized void rotate() throws RotateQueueException {
        File file = new File(dir, baseFilename + LOG_FILE_ID_DELIMITER + System.nanoTime());
        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RotateQueueException(e);
            }
        }
        logFiles.addLast(file);

        if (out != null) {
            closeWriter();
        }
        try {
            out = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new RotateQueueException(e);
        }
    }

    private void closeReader() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignore) {
            } finally {
                in = null;
            }
        }
    }

    private void closeWriter() {
        if (out != null) {
            try {
                out.flush();
            } catch (IOException ignore) {
            } finally {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
                out = null;
            }
        }
    }



    @Override
    public synchronized void offer(T obj) {
        if (out == null) {
            try {
                rotate();
            } catch (RotateQueueException e) {
                throw new QueueOperationException(e);
            }
        }
        try {
            String encoded = codec.encode(obj);
            out.write(encoded);
            out.write(ENTITY_DELIMITER);
            out.flush();
        } catch (IOException e) {
            throw new QueueOperationException(e);
        }
    }

    @Override
    public T poll() {
        if (in == null && logFiles.size() <= 1) {
            return null;
        }
        if (in == null) {
            File file = logFiles.getFirst();
            try {
                in = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                logFiles.removeFirst();
                throw new QueueOperationException(e);
            }
        }
        try {
            String line = in.readLine();
            if (line == null) {
                //file end
                closeReader();
                File src = logFiles.removeFirst();
                src.delete();
                //读下一个文件
                return poll();
            }
            line = line.trim();
            if (line.equals("")) {
                //读取下一行
                return poll();
            }
            return codec.decode(line);
        } catch (IOException e) {
            throw new QueueOperationException(e);
        }
    }


}
