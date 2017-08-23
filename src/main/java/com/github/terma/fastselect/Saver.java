package com.github.terma.fastselect;

import com.github.terma.fastselect.data.Data;
import com.github.terma.fastselect.utils.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Usage
 * <pre>
 *     RandomAccessFile f = ...;
 *     Data[] data = ...;
 *
 *     Saver s = new Saver(DataClass.class, randomAccessFile);
 *     for (Data d : data) s.saveData(data); // store one column (data) any order
 *     s.save(); // store header
 * </pre>
 */
@SuppressWarnings("WeakerAccess")
public class Saver implements Closeable {

    private ByteBuffer headerBuffer;
    private FileChannel fileChannel;
    private ColumnInfo[] info;
    private int columnIndex;

    public Saver(final List<FastSelect.Column> columns, final int size, final FileChannel fileChannel) throws IOException {
        this.fileChannel = fileChannel;

        info = new ColumnInfo[columns.size()];
        headerBuffer = ByteBuffer.allocate(1024 * 1024);
        headerBuffer.putInt(Data.STORAGE_FORMAT_VERSION);
        headerBuffer.putInt(size);
        headerBuffer.putInt(columns.size());

        // write columns meta
        for (int i = 0; i < columns.size(); i++) {
            final FastSelect.Column column = columns.get(i);
            IOUtils.writeString(headerBuffer, column.data.getClass().getName());
            IOUtils.writeString(headerBuffer, column.name);
            final int headerPosition = headerBuffer.position();
            headerBuffer.putLong(0); // data position placeholder
            headerBuffer.putInt(0); // data size placeholder
            info[i] = new ColumnInfo(headerPosition);
        }

        // set position in channel after header
        fileChannel.position(headerBuffer.position());
    }

    public void saveData(final Data data) throws IOException {
        long position;
        if (columnIndex > 0) position = info[columnIndex - 1].position + info[columnIndex - 1].size;
        else position = headerBuffer.position();

        final int diskSpace = data.getDiskSpace();
        final ByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, position, diskSpace);
        data.save(buffer);

        ColumnInfo columnInfo = info[columnIndex];
        columnInfo.position = position;
        columnInfo.size = diskSpace;
        columnIndex++;
    }

    public void save() throws IOException {
        // set column data position and size
        for (ColumnInfo columnInfo : info) {
            headerBuffer.putLong(columnInfo.headerPosition, columnInfo.position);
            headerBuffer.putInt(columnInfo.headerPosition + Data.LONG_BYTES, columnInfo.size);
        }

        headerBuffer.flip();
        fileChannel.write(headerBuffer, 0);
    }

    @Override
    public void close() throws IOException {
        fileChannel = null;
    }

    private static class ColumnInfo {
        int headerPosition;
        long position;
        int size;

        public ColumnInfo(int headerPosition) {
            this.headerPosition = headerPosition;
        }
    }
}
