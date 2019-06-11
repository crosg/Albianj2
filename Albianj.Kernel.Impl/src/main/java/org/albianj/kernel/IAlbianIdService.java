package org.albianj.kernel;

import java.math.BigInteger;

public interface IAlbianIdService {
    BigInteger genId();
    BigInteger genId(String networkName);
}
