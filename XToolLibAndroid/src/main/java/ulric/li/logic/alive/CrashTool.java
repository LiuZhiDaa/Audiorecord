package ulric.li.logic.alive;

import ulric.li.utils.UtilsLog;

public class CrashTool implements Thread.UncaughtExceptionHandler {
    private static final long VALUE_LONG_THREAD_DELAY_TIME = 3000;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        UtilsLog.crashLog(ex);

        try {
            Thread.sleep(VALUE_LONG_THREAD_DELAY_TIME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public static void init() {
        Thread.setDefaultUncaughtExceptionHandler(new CrashTool());
    }
}