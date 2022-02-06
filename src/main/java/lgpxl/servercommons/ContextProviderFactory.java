package lgpxl.servercommons;

public class ContextProviderFactory {
    public static ContextProvider CONTEXT;

    public static void setContext(ContextProvider provider){
        if(CONTEXT != null){
            throw new IllegalStateException("context is already set");
        }
        CONTEXT = provider;
    }
}
