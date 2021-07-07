package fr.proline.zero.modules;

public interface IZeroModule {

    boolean isProcessAlive();

    void init() throws Exception;

    void start() throws Exception;

    void stop() throws Exception;

    String getModuleName();

}
