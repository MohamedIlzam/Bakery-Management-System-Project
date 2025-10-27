package com.example.KodikaraGroupBusinessManagementApplication.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    // Existing Counters
    private static final AtomicLong saleCounter = new AtomicLong(0);
    private static final AtomicLong detailCounter = new AtomicLong(0);
    private static final AtomicLong shopCounter = new AtomicLong(0);
    private static final AtomicLong vehicleCounter = new AtomicLong(0);
    private static final AtomicLong userCounter = new AtomicLong(0);
    private static final AtomicLong driverCounter = new AtomicLong(0);
    private static final AtomicLong dailyReportCounter = new AtomicLong(0);
    private static final AtomicLong monthlyReportCounter = new AtomicLong(0);
    private static final AtomicLong fairDeliveryCounter = new AtomicLong(0);
    private static final AtomicLong fairItemCounter = new AtomicLong(0);
    private static final AtomicLong productCounter = new AtomicLong(0);

    public static String generate(String prefix) {
        switch (prefix) {
            case "SALE":
                return "SALE" + String.format("%06d", saleCounter.incrementAndGet());
            case "SHOP":
                return "SHOP" + String.format("%03d", shopCounter.incrementAndGet());
            case "VEH":
                return "VEH" + String.format("%07d", vehicleCounter.incrementAndGet());
            case "SDET":
                return "SDET" + String.format("%06d", detailCounter.incrementAndGet());
            case "DRV":
                return "DRV" + String.format("%07d", driverCounter.incrementAndGet());
            case "DREP":
                return "DREP" + String.format("%06d", dailyReportCounter.incrementAndGet());
            case "MREP":
                return "MREP" + String.format("%06d", monthlyReportCounter.incrementAndGet());
            case "FDEL":
                return "FDEL" + String.format("%06d", fairDeliveryCounter.incrementAndGet());
            case "FITE":
                return "FITE" + String.format("%06d", fairItemCounter.incrementAndGet());
            case "USER":
                return "USER" + String.format("%06d", userCounter.incrementAndGet());
            case "PROD":
                return "PROD" + String.format("%03d", productCounter.incrementAndGet());
            default:
                return prefix + String.format("%06d", System.currentTimeMillis() % 1000000);
        }
    }


    public static String saleId() {
        return generate("SALE");
    }

    public static String saleDetailId() {
        return generate("SDET");
    }

    public static String userId() {
        return generate("USER");
    }
    public static String productId() { return generate("PROD"); }
    public static String driverId() { return generate("DRV"); }
    public static String vehicleId() { return generate("VEH"); }
    public static String shopId() { return generate("SHOP"); }
    public static String dailyReportId() { return generate("DREP"); }
    public static String monthlyReportId() { return generate("MREP"); }
    public static String fairDeliveryId() { return generate("FDEL"); }
    public static String fairItemId() { return generate("FITE"); }
}