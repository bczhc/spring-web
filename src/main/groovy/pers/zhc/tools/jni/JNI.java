/*翟灿hhh*/
package pers.zhc.tools.jni;

import pers.zhc.web.Global;

import java.io.File;
import java.io.IOException;

/**
 * @author bczhc
 */
public class JNI {
    private static boolean hasLoadedLib = false;

    private synchronized static void loadLib() {
        if (!hasLoadedLib) {
            final File libFile = new File(Global.LIB_PATH);
            try {
                System.load(libFile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            hasLoadedLib = true;
        }
    }

    public static class Sqlite3 {
        static {
            loadLib();
        }

        /**
         * Open sqlite database.
         *
         * @param path sqlite database path, if not exists, it'll create a new sqlite database
         * @return the associated id which is the address of an handler object in JNI.
         */
        public static native long open(String path) throws RuntimeException;

        /**
         * Close sqlite database
         *
         * @param id the associated id
         */
        public static native void close(long id) throws RuntimeException;

        /**
         * Execute a sqlite command.
         *
         * @param id  the associated id
         * @param cmd command
         */
        public static native void exec(long id, String cmd, SqliteExecCallback callback) throws RuntimeException;

        /**
         * Check if the database is corrupted.
         *
         * @param id id
         * @return result
         */
        public static native boolean checkIfCorrupt(long id);

        /**
         * Compile sqlite statement.
         *
         * @param id  id
         * @param sql sqlite statement
         * @return the address of the statement object in JNI, which is a "statement object handler"
         */
        public static native long compileStatement(long id, String sql) throws RuntimeException;

        public static class Statement {
            static {
                loadLib();
            }

            /* Statement methods start: */
            public static native void bind(long stmtId, int row, int a) throws RuntimeException;

            public static native void bind(long stmtId, int row, long a) throws RuntimeException;

            public static native void bind(long stmtId, int row, double a) throws RuntimeException;

            public static native void bindText(long stmtId, int row, String s) throws RuntimeException;

            public static native void bindNull(long stmtId, int row) throws RuntimeException;

            public static native void reset(long stmtId) throws RuntimeException;

            public static native void bindBlob(long stmtId, int row, byte[] bytes, int size) throws RuntimeException;

            public static native void step(long stmtId) throws RuntimeException;

            public static native void finalize(long stmtId) throws RuntimeException;

            /**
             * Get cursor.
             *
             * @param stmtId native statement object address
             * @return native cursor object address
             */
            public static native long getCursor(long stmtId);

            /**
             * Call this rather than {@link #step(long)} when the statement returns values like `select` statements.
             *
             * @param stmtId statement native address
             * @return {@value pers.zhc.tools.utils.sqlite.SQLite3#SQLITE_ROW} if succeeded, otherwise others.
             */
            public static native int stepRow(long stmtId);

            /**
             * @param stmtId statement native address
             * @param name   column name
             * @return index, the leftmost value is 0
             */
            public static native int getIndexByColumnName(long stmtId, String name);
            /* Statement methods end. */
        }

        public static class Cursor {
            static {
                loadLib();
            }

            /* Cursor methods start. */
            public static native void reset(long cursorId) throws RuntimeException;

            public static native boolean step(long cursorId) throws RuntimeException;

            public static native byte[] getBlob(long cursorId, int column);

            public static native String getText(long cursorId, int column);

            public static native double getDouble(long cursorId, int column);

            public static native long getLong(long cursorId, int column);

            public static native int getInt(long cursorId, int column);
            /* Cursor methods end. */
        }

        public interface SqliteExecCallback {
            /**
             * Callback when {@link Sqlite3#exec(long, String, SqliteExecCallback)} is called.
             *
             * @param contents content in database
             * @return whether to continue searching:
             * 0: continue
             * non-zero: interrupt searching
             */
            int callback(String[] contents);
        }
    }

    public static class Struct {
        static {
            loadLib();
        }

        public static final int MODE_BIG_ENDIAN = 0;
        public static final int MODE_LITTLE_ENDIAN = 1;

        public static native void packShort(short value, byte[] dest, int offset, int mode);

        public static native void packInt(int value, byte[] dest, int offset, int mode);

        public static native void packLong(long value, byte[] dest, int offset, int mode);

        public static native void packFloat(float value, byte[] dest, int offset, int mode);

        public static native void packDouble(double value, byte[] dest, int offset, int mode);

        public static native short unpackShort(byte[] bytes, int offset, int mode);

        public static native int unpackInt(byte[] bytes, int offset, int mode);

        public static native long unpackLong(byte[] bytes, int offset, int mode);

        public static native float unpackFloat(byte[] bytes, int offset, int mode);

        public static native double unpackDouble(byte[] bytes, int offset, int mode);
    }
}
