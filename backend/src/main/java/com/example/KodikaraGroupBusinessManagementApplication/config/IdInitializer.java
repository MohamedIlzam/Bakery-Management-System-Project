package com.example.KodikaraGroupBusinessManagementApplication.config;

import com.example.KodikaraGroupBusinessManagementApplication.model.*;
import com.example.KodikaraGroupBusinessManagementApplication.util.IdGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class IdInitializer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) throws Exception {
        System.out.println(">>> Initializing IdGenerator counters from existing database records...");

        // Standard tables with dedicated counters
        initializeCounter("USER", User.class, "userId");
        initializeCounter("PROD", Product.class, "proId");
        initializeCounter("SHOP", Shop.class, "shopId");
        initializeCounter("VEH", Vehicle.class, "vehicleId");
        initializeCounter("DRV", Driver.class, "driverId");
        initializeCounter("SALE", Sale.class, "saleId");
        initializeCounter("SDET", SaleDetail.class, "sdetailId");
        initializeCounter("DREP", DailyReport.class, "dreportId");
        initializeCounter("MREP", MonthlyReport.class, "mreportId");
        initializeCounter("FDEL", FairDelivery.class, "deliveryId");
        initializeCounter("FITE", FairDeliveryItem.class, "itemId");
        initializeCounter("SUPP", ShopSupply.class, "supplyId");
        initializeCounter("SITE", ShopSupplyItem.class, "itemId");

        // Reports that share counters (we retrieve max across prefix patterns)
        initializeSharedCounter("FDREP", "freportId", FairDeliveryReport.class);
        initializeSharedCounter("FMREP", "freportId", FairDeliveryReport.class);
        initializeSharedCounter("SDREP", "sreportId", ShopSupplyReport.class);
        initializeSharedCounter("SMREP", "sreportId", ShopSupplyReport.class);

        System.out.println(">>> IdGenerator counters initialization complete!");
    }

    private void initializeCounter(String prefix, Class<?> entityClass, String idFieldName) {
        try {
            String queryStr = "SELECT MAX(e." + idFieldName + ") FROM " + entityClass.getSimpleName() + " e";
            Object result = entityManager.createQuery(queryStr).getSingleResult();
            if (result != null) {
                String maxId = result.toString();
                long maxNum = IdGenerator.extractNumber(maxId);
                IdGenerator.setCounter(prefix, maxNum);
                System.out
                        .println("  Counter for " + prefix + " set to " + maxNum + " (Max existing ID: " + maxId + ")");
            } else {
                IdGenerator.setCounter(prefix, 0);
                System.out.println("  Counter for " + prefix + " set to 0 (No existing records)");
            }
        } catch (Exception e) {
            System.err.println("  Failed to initialize counter for " + prefix + " (" + entityClass.getSimpleName()
                    + "): " + e.getMessage());
        }
    }

    private void initializeSharedCounter(String prefix, String idFieldName, Class<?> entityClass) {
        try {
            String queryStr = "SELECT e." + idFieldName + " FROM " + entityClass.getSimpleName() + " e WHERE e."
                    + idFieldName + " LIKE :prefix";
            List<String> ids = entityManager.createQuery(queryStr, String.class)
                    .setParameter("prefix", prefix + "%")
                    .getResultList();
            long maxNum = 0;
            for (String id : ids) {
                long num = IdGenerator.extractNumber(id);
                if (num > maxNum) {
                    maxNum = num;
                }
            }
            IdGenerator.setCounter(prefix, maxNum);
            System.out.println(
                    "  Shared counter for " + prefix + " set to " + maxNum + " (Scanned " + ids.size() + " records)");
        } catch (Exception e) {
            System.err.println("  Failed to initialize shared counter for " + prefix + " ("
                    + entityClass.getSimpleName() + "): " + e.getMessage());
        }
    }
}
