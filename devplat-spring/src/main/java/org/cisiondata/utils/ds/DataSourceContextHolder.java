package org.cisiondata.utils.ds;

public class DataSourceContextHolder {

	private static final ThreadLocal<String> context = new ThreadLocal<String>();

    public static void setDataSource(String value) {
        context.set(value);
    }

    public static String getDataSource() {
        return null == context.get() ? DataSource.MASTER : context.get();
    }

    public static void clearDataSource() {
        context.remove();
    }
	
}
