package com.enka.prueba.net;

import lombok.Data;

@Data
public class CodigosTransferencia {
    public static final String ID = "999";
    public static final String OK = "000";
    public static final String VOLUME_UP = "001";
    public static final String VOLUME_DOWN = "002";
    //Actual volume en enviará el código y posteriormente el volumén separado por una coma
    public static final String ACTUAL_VOLUME = "003";
    public static final String SHUTDOWN_PC = "004";
}
