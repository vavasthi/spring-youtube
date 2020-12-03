package in.springframework.learning.tutorial.security;

import java.util.HashSet;
import java.util.Set;

public enum Role {
    USER(0x01),
    TESTER(0x01 << 1),
    ADMIN(0x01 << 2),
    REFRESH(0x01 << 3),
    ANONYMOUS(0x01 << 4);

    private final int mask;

    Role(int mask) {
        this.mask = mask;
    }

    public static Set<Role> createFromMask(long mask) {
        Set<Role> returnValue = new HashSet<>();
        for (Role r : values()) {
            if ((r.mask & mask) != 0) {
                returnValue.add(r);
            }
        }
        return returnValue;
    }
}
