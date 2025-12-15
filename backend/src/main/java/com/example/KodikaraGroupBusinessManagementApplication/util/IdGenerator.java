package com.example.KodikaraGroupBusinessManagementApplication.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

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
    private static final AtomicLong fairDeliveryReportCounter = new AtomicLong(0);
    private static final AtomicLong shopSupplyCounter = new AtomicLong(0);
    private static final AtomicLong shopSupplyItemCounter = new AtomicLong(0);
    private static final AtomicLong shopSupplyReportCounter = new AtomicLong(0);

    public static String generate(String prefix) {
        System.out.println("ID_GEN_DEBUG: Received prefix: '" + prefix + "'");
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
            case "FREP":
                return "FREP" + String.format("%06d", productCounter.incrementAndGet());
            case "FDREP":
                System.out.println("ID_GEN_DEBUG: Hit FDREP Case!");
                return "FDREP" + String.format("%05d", fairDeliveryReportCounter.incrementAndGet());
            case "FMREP":
                System.out.println("ID_GEN_DEBUG: Hit FMREP Case!");
                return "FMREP" + String.format("%05d", fairDeliveryReportCounter.incrementAndGet());
            case "USER":
                return "USER" + String.format("%06d", userCounter.incrementAndGet());
            case "PROD":
                return "PROD" + String.format("%03d", productCounter.incrementAndGet());
            case "SUPP":
                return "SUPP" + String.format("%06d", shopSupplyCounter.incrementAndGet());
            case "SITE":
                return "SITE" + String.format("%06d", shopSupplyItemCounter.incrementAndGet());
            case "SREP":
                return "SREP" + String.format("%06d", shopSupplyReportCounter.incrementAndGet());
            case "SDREP":
                System.out.println("ID_GEN_DEBUG: Hit SDREP Case!");
                return "SDREP" + String.format("%05d", shopSupplyReportCounter.incrementAndGet());
            case "SMREP":
                System.out.println("ID_GEN_DEBUG: Hit SMREP Case!");
                return "SMREP" + String.format("%05d", shopSupplyReportCounter.incrementAndGet());
            default:
                System.out.println("ID_GEN_DEBUG: Hit DEFAULT Case! (This is the error)");
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
    public static String fairDeliveryReportId() { return generate("FREP"); }
    public static String fairDailyReportId() { return generate("FDREP"); }
    public static String shopSupplyId() { return generate("SUPP"); }
    public static String shopSupplyItemId() { return generate("SITE"); }
    public static String shopSupplyReportId() { return generate("SREP"); }
}