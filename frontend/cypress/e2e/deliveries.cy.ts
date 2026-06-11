describe("Deliveries E2E Tests", () => {
  beforeEach(() => {
    // 1. Mock Authentication APIs
    cy.intercept("POST", "**/api/auth/login", {
      statusCode: 200,
      body: {
        success: true,
        role: "ROLE_OWNER",
        message: "Login successful",
        userId: "USER000005",
      },
    }).as("loginReq");

    cy.intercept("GET", "**/api/salesman/profile", {
      statusCode: 200,
      body: {
        userId: "USER000005",
        username: "owner",
        role: "ROLE_OWNER",
        recoveryEmail: "owner@bakery.com",
        approved: true,
      },
    }).as("profileReq");

    // 2. Mock Master Data APIs
    // Products
    cy.intercept("GET", "**/api/products", {
      statusCode: 200,
      body: [
        { proId: "PROD001", name: "Fish Bun", category: "Buns", unitPrice: 80.0, active: true },
        { proId: "PROD002", name: "White Bread", category: "Bread & Loaves", unitPrice: 120.0, active: true },
      ],
    }).as("getProducts");

    // Drivers
    cy.intercept("GET", "**/api/drivers", {
      statusCode: 200,
      body: [
        { driverId: "DRV0000001", name: "Saman Kumara", contact: "0771112223" },
      ],
    }).as("getDrivers");

    // Vehicles
    cy.intercept("GET", "**/api/vehicles", {
      statusCode: 200,
      body: [
        { vehicleId: "VEH0000001", vehicleNo: "WP WP-1234", vehicleType: "Van" },
      ],
    }).as("getVehicles");

    // Shops
    cy.intercept("GET", "**/api/shops", {
      statusCode: 200,
      body: [
        { shopId: "SHOP001", shopName: "Kodikara Grocery", ownerName: "Kamal", contactNo: "0771234567", address: "Galle" },
      ],
    }).as("getShops");

    // Visit Login Page & authenticate
    cy.visit("/login");
    cy.get("#username").type("owner");
    cy.get("#password").type("owner1");
    cy.get('button[type="submit"]').click();
    cy.url().should("include", "/dashboard");
  });

  describe("Fair Delivery Tests", () => {
    beforeEach(() => {
      // Mock initial empty fair deliveries list
      cy.intercept("GET", "**/api/fair-deliveries", {
        statusCode: 200,
        body: [],
      }).as("getFairDeliveries");

      cy.visit("/fair-delivery");
    });

    it("should load the Fair Delivery page with empty list", () => {
      cy.contains("h1", "Fair Delivery Management").should("be.visible");
      cy.contains("No deliveries found matching your filter.").should("be.visible");
    });

    it("should support creating, editing, and deleting a Fair Delivery", () => {
      // Setup intercepts for creation
      cy.intercept("POST", "**/api/fair-deliveries", {
        statusCode: 201,
        body: {
          deliveryId: "FDLV0001",
          fairName: "Galle Fair",
          deliveryDate: "2026-06-12",
          driverId: "DRV0000001",
          vehicleId: "VEH0000001",
          status: "OUT",
          extraPayments: 100.0,
          tax: 50.0,
          dieselAmount: 1500.0,
          items: [
            { productId: "PROD001", qtySent: 100, unitPrice: 80.0, qtyRemaining: 10, qtyExpired: 5 }
          ]
        }
      }).as("createFairDelivery");

      // Setup intercept for updated list
      cy.intercept("GET", "**/api/fair-deliveries", {
        statusCode: 200,
        body: [
          {
            deliveryId: "FDLV0001",
            fairName: "Galle Fair",
            deliveryDate: "2026-06-12",
            driverId: "DRV0000001",
            vehicleId: "VEH0000001",
            status: "OUT",
            extraPayments: 100.0,
            tax: 50.0,
            dieselAmount: 1500.0,
            items: [
              { productId: "PROD001", qtySent: 100, unitPrice: 80.0, qtyRemaining: 10, qtyExpired: 5 }
            ]
          }
        ]
      }).as("getFairDeliveriesUpdated");

      // Fill form
      cy.get("#fairName").type("Galle Fair");
      
      // Select Vehicle
      cy.get("#vehicle").click();
      cy.get('[role="option"]').contains("WP WP-1234").click();

      // Select Driver
      cy.get("#driver").click();
      cy.get('[role="option"]').contains("Saman Kumara").click();

      // Select Status
      cy.get("#status").click();
      cy.get('[role="option"]').contains("Out (Going to fair)").click();

      // Select Product
      cy.get("table tbody tr").first().find("button").first().click(); // Open product dropdown
      cy.get('[role="option"]').contains("Fish Bun").click();

      // Input Qty Sent, Returned, Expired
      cy.get("table tbody tr").first().find('input[type="number"]').eq(0).clear().type("100");
      cy.get("table tbody tr").first().find('input[type="number"]').eq(1).clear().type("10");
      cy.get("table tbody tr").first().find('input[type="number"]').eq(2).clear().type("5");

      // Input Expenses
      cy.get("#tax").clear().type("50");
      cy.get("#extraPayments").clear().type("100");
      cy.get("#dieselAmount").clear().type("1500");

      // Submit
      cy.contains("button", "Save Delivery").click();
      cy.wait("@createFairDelivery");
      cy.wait("@getFairDeliveriesUpdated");

      // Verify delivery listed
      cy.contains("h3", "Galle Fair").should("be.visible");
      cy.contains("span", "OUT").should("be.visible");

      // Setup mock for update
      cy.intercept("PUT", "**/api/fair-deliveries/*", {
        statusCode: 200,
        body: {
          deliveryId: "FDLV0001",
          fairName: "Galle Fair Updated",
          deliveryDate: "2026-06-12",
          driverId: "DRV0000001",
          vehicleId: "VEH0000001",
          status: "RETURN",
          extraPayments: 100.0,
          tax: 50.0,
          dieselAmount: 1500.0,
          items: [
            { productId: "PROD001", qtySent: 100, unitPrice: 80.0, qtyRemaining: 10, qtyExpired: 5 }
          ]
        }
      }).as("updateFairDelivery");

      // Setup mock list for update
      cy.intercept("GET", "**/api/fair-deliveries", {
        statusCode: 200,
        body: [
          {
            deliveryId: "FDLV0001",
            fairName: "Galle Fair Updated",
            deliveryDate: "2026-06-12",
            driverId: "DRV0000001",
            vehicleId: "VEH0000001",
            status: "RETURN",
            extraPayments: 100.0,
            tax: 50.0,
            dieselAmount: 1500.0,
            items: [
              { productId: "PROD001", qtySent: 100, unitPrice: 80.0, qtyRemaining: 10, qtyExpired: 5 }
            ]
          }
        ]
      }).as("getFairDeliveriesUpdated2");

      // Edit delivery
      cy.contains("h3", "Galle Fair").closest(".border").find("button").first().click({ force: true }); // Click Edit icon
      cy.get("#fairName").clear().type("Galle Fair Updated");
      cy.get("#status").click();
      cy.get('[role="option"]').contains("Return (Came back)").click();
      cy.contains("button", "Update Delivery").click();
      cy.wait("@updateFairDelivery");

      // Verify updated status
      cy.contains("h3", "Galle Fair Updated").should("be.visible");
      cy.contains("span", "RETURN").should("be.visible");

      // Setup mock list for delete
      cy.intercept("DELETE", "**/api/fair-deliveries/*", {
        statusCode: 204,
        body: {}
      }).as("deleteFairDelivery");

      cy.intercept("GET", "**/api/fair-deliveries", {
        statusCode: 200,
        body: []
      }).as("getFairDeliveriesEmpty");

      // Delete delivery
      cy.on("window:confirm", () => true);
      cy.contains("h3", "Galle Fair Updated").closest(".border").find("button").eq(1).click({ force: true }); // Click Delete icon
      cy.wait("@deleteFairDelivery");
      cy.wait("@getFairDeliveriesEmpty");

      // Verify empty list
      cy.contains("No deliveries found matching your filter.").should("be.visible");
    });
  });

  describe("Shop Delivery Tests", () => {
    beforeEach(() => {
      // Mock initial empty shop deliveries list
      cy.intercept("GET", "**/api/shop-supplies", {
        statusCode: 200,
        body: [],
      }).as("getShopSupplies");

      cy.visit("/shop-delivery");
    });

    it("should load the Shop Delivery page with empty list", () => {
      cy.contains("h1", "Deliver to Shops").should("be.visible");
      cy.contains("Saved Deliveries (0)").should("be.visible");
    });

    it("should support creating, completing, paying, and deleting a Shop Delivery", () => {
      // Setup mock for creation of assignment
      cy.intercept("POST", "**/api/shop-supplies", {
        statusCode: 201,
        body: {
          supplyId: "SMPL0001",
          shopId: "SHOP001",
          shopName: "Kodikara Grocery",
          salesmanId: "USER000005",
          driverId: "DRV0000001",
          driverName: "Saman Kumara",
          vehicleId: "VEH0000001",
          vehicleNo: "WP WP-1234",
          supplyDate: "2026-06-12",
          paymentStatus: "UNPAID",
          paidAmount: 0.0,
          outstandingAmount: 0.0,
          totalAmount: 0.0,
          items: []
        }
      }).as("createShopSupply");

      // Setup mock updated list (one assignment)
      cy.intercept("GET", "**/api/shop-supplies", {
        statusCode: 200,
        body: [
          {
            supplyId: "SMPL0001",
            shopId: "SHOP001",
            shopName: "Kodikara Grocery",
            salesmanId: "USER000005",
            driverId: "DRV0000001",
            driverName: "Saman Kumara",
            vehicleId: "VEH0000001",
            vehicleNo: "WP WP-1234",
            supplyDate: "2026-06-12",
            paymentStatus: "UNPAID",
            paidAmount: 0.0,
            outstandingAmount: 0.0,
            totalAmount: 0.0,
            items: []
          }
        ]
      }).as("getShopSuppliesUpdated");

      // Select Shop
      cy.contains("label", "Shop").parent().find("button").click();
      cy.get('[role="option"]').contains("Kodikara Grocery").click();

      // Select Driver
      cy.contains("label", "Driver").parent().find("button").click();
      cy.get('[role="option"]').contains("Saman Kumara").click();

      // Select Vehicle
      cy.contains("label", "Vehicle").parent().find("button").click();
      cy.get('[role="option"]').contains("WP WP-1234").click();

      // Save Assignment without products
      cy.on("window:confirm", () => true);
      cy.contains("button", "Save Assignment").click();
      cy.wait("@createShopSupply");
      cy.wait("@getShopSuppliesUpdated");

      // Verify assignment visible
      cy.contains("h3", "Kodikara Grocery").should("be.visible");
      cy.contains("span", "ASSIGNED - PENDING").should("be.visible");

      // Setup mock for update (Fulfill delivery)
      cy.intercept("PUT", "**/api/shop-supplies/*", {
        statusCode: 200,
        body: {
          supplyId: "SMPL0001",
          shopId: "SHOP001",
          shopName: "Kodikara Grocery",
          salesmanId: "USER000005",
          driverId: "DRV0000001",
          driverName: "Saman Kumara",
          vehicleId: "VEH0000001",
          vehicleNo: "WP WP-1234",
          supplyDate: "2026-06-12",
          paymentStatus: "PARTIAL",
          paidAmount: 0.0,
          outstandingAmount: 8000.0,
          totalAmount: 8000.0,
          items: [
            { productId: "PROD001", productName: "Fish Bun", quantity: 100, returnQuantity: 0, expiredQuantity: 0, price: 80.0 }
          ]
        }
      }).as("updateShopSupply");

      // Setup mock list updated
      cy.intercept("GET", "**/api/shop-supplies", {
        statusCode: 200,
        body: [
          {
            supplyId: "SMPL0001",
            shopId: "SHOP001",
            shopName: "Kodikara Grocery",
            salesmanId: "USER000005",
            driverId: "DRV0000001",
            driverName: "Saman Kumara",
            vehicleId: "VEH0000001",
            vehicleNo: "WP WP-1234",
            supplyDate: "2026-06-12",
            paymentStatus: "PARTIAL",
            paidAmount: 0.0,
            outstandingAmount: 8000.0,
            totalAmount: 8000.0,
            items: [
              { productId: "PROD001", productName: "Fish Bun", quantity: 100, returnQuantity: 0, expiredQuantity: 0, price: 80.0 }
            ]
          }
        ]
      }).as("getShopSuppliesUpdated2");

      // Click Fulfill button
      cy.contains("button", "Fulfill").click({ force: true });

      // Add a product in row 1
      cy.get("table tbody tr").first().find("button").first().click();
      cy.get('[role="option"]').contains("Fish Bun").click();
      cy.get("table tbody tr").first().find('input[type="number"]').eq(0).clear().type("100");

      // Complete delivery
      cy.contains("button", "Complete Delivery").click();
      cy.wait("@updateShopSupply");
      cy.wait("@getShopSuppliesUpdated2");

      // Verify outstanding status is showing
      cy.contains("Outstanding: Rs. 8000.00").should("be.visible");

      // Setup mock for adding payment
      cy.intercept("PUT", "**/api/shop-supplies/*/payment**", {
        statusCode: 200,
        body: {
          supplyId: "SMPL0001",
          shopId: "SHOP001",
          shopName: "Kodikara Grocery",
          paymentStatus: "COMPLETED",
          paidAmount: 8000.0,
          outstandingAmount: 0.0,
          totalAmount: 8000.0,
          items: [
            { productId: "PROD001", productName: "Fish Bun", quantity: 100, returnQuantity: 0, expiredQuantity: 0, price: 80.0 }
          ]
        }
      }).as("addPayment");

      // Setup mock list after payment
      cy.intercept("GET", "**/api/shop-supplies", {
        statusCode: 200,
        body: [
          {
            supplyId: "SMPL0001",
            shopId: "SHOP001",
            shopName: "Kodikara Grocery",
            salesmanId: "USER000005",
            driverId: "DRV0000001",
            driverName: "Saman Kumara",
            vehicleId: "VEH0000001",
            vehicleNo: "WP WP-1234",
            supplyDate: "2026-06-12",
            paymentStatus: "COMPLETED",
            paidAmount: 8000.0,
            outstandingAmount: 0.0,
            totalAmount: 8000.0,
            items: [
              { productId: "PROD001", productName: "Fish Bun", quantity: 100, returnQuantity: 0, expiredQuantity: 0, price: 80.0 }
            ]
          }
        ]
      }).as("getShopSuppliesUpdated3");

      // Record payment
      cy.contains("h3", "Kodikara Grocery").closest(".border").find("button").eq(1).click({ force: true }); // Click wallet icon
      cy.contains("label", "Payment Amount").parent().find("input").type("8000");
      cy.contains("button", "Save Payment").click();
      cy.wait("@addPayment");
      cy.wait("@getShopSuppliesUpdated3");

      // Verify COMPLETED status badge
      cy.contains("span", "COMPLETED").should("be.visible");

      // Setup mock list for delete
      cy.intercept("DELETE", "**/api/shop-supplies/*", {
        statusCode: 204,
        body: {}
      }).as("deleteShopSupply");

      cy.intercept("GET", "**/api/shop-supplies", {
        statusCode: 200,
        body: []
      }).as("getShopSuppliesEmpty");

      // Delete delivery
      cy.on("window:confirm", () => true);
      cy.contains("h3", "Kodikara Grocery").closest(".border").find("button").last().click({ force: true }); // Click delete icon
      cy.wait("@deleteShopSupply");
      cy.wait("@getShopSuppliesEmpty");

      // Verify empty list
      cy.contains("Saved Deliveries (0)").should("be.visible");
    });
  });
});
