package com.example.KodikaraGroupBusinessManagementApplication.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final AtomicLong saleCounter = new AtomicLong(0);
    private static final AtomicLong detailCounter = new AtomicLong(0);
    private static final AtomicLong shopCounter = new AtomicLong(0);
    private static final AtomicLong vehicleCounter = new AtomicLong(0);
    private static final AtomicLong userCounter = new AtomicLong(0);

    public static String generate(String prefix) {
        switch (prefix) {
            case "SALE":
                return "SALE" + String.format("%06d", saleCounter.incrementAndGet());
            case "SHOP":
                return "SHOP" + String.format("%06d", shopCounter.incrementAndGet());
            case "VEH":
                return "VEH" + String.format("%06d", vehicleCounter.incrementAndGet());
            case "SDETA":
                return "SDETA" + String.format("%06d", detailCounter.incrementAndGet());
            case "DRV":
                return "DRV" + String.format("%06d", userCounter.incrementAndGet());
            default:
                return prefix + String.format("%06d", System.currentTimeMillis() % 1000000);
        }
    }

    public static String saleId() {
        return generate("SALE");
    }

    public static String saleDetailId() {
        return generate("SDETA");
    }
}