describe("Manage Dashboard E2E Tests", () => {
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

    // 2. Mock CRUD APIs
    // Products
    cy.intercept("GET", "**/api/products", {
      statusCode: 200,
      body: [
        { proId: "PROD001", name: "Fish Bun", category: "Buns", unitPrice: 80.0, active: true },
        { proId: "PROD002", name: "White Bread", category: "Bread & Loaves", unitPrice: 120.0, active: true },
      ],
    }).as("getProducts");

    cy.intercept("POST", "**/api/products", {
      statusCode: 201,
      body: { proId: "PROD003", name: "Egg Bun", category: "Buns", unitPrice: 90.0, active: true },
    }).as("createProduct");

    cy.intercept("PUT", "**/api/products/*", {
      statusCode: 200,
      body: { proId: "PROD001", name: "Fish Bun Special", category: "Buns", unitPrice: 85.0, active: true },
    }).as("updateProduct");

    cy.intercept("DELETE", "**/api/products/*", {
      statusCode: 204,
      body: {},
    }).as("deleteProduct");

    // Shops
    cy.intercept("GET", "**/api/shops", {
      statusCode: 200,
      body: [
        { shopId: "SHOP001", shopName: "Kodikara Grocery", ownerName: "Kamal", contactNo: "0771234567", address: "Galle" },
      ],
    }).as("getShops");

    cy.intercept("POST", "**/api/shops", {
      statusCode: 201,
      body: { shopId: "SHOP002", shopName: "Silva Stores", ownerName: "Nimal", contactNo: "0717654321", address: "Matara" },
    }).as("createShop");

    cy.intercept("PUT", "**/api/shops/*", {
      statusCode: 200,
      body: { shopId: "SHOP001", shopName: "Kodikara Supermarket", ownerName: "Kamal", contactNo: "0771234567", address: "Galle" },
    }).as("updateShop");

    cy.intercept("DELETE", "**/api/shops/*", {
      statusCode: 204,
      body: {},
    }).as("deleteShop");

    // Users
    cy.intercept("GET", "**/api/salesman/all", {
      statusCode: 200,
      body: [
        { userId: "USER000006", username: "salesman1", role: "ROLE_SALESMAN", recoveryEmail: "sales1@bakery.com" },
      ],
    }).as("getUsers");

    cy.intercept("POST", "**/api/salesman/create", {
      statusCode: 201,
      body: { userId: "USER000007", username: "salesman2", role: "ROLE_SALESMAN", recoveryEmail: "sales2@bakery.com" },
    }).as("createUser");

    cy.intercept("PUT", "**/api/salesman/*", {
      statusCode: 200,
      body: { userId: "USER000006", username: "salesman1-edit", role: "ROLE_SALESMAN" },
    }).as("updateUser");

    cy.intercept("DELETE", "**/api/salesman/*", {
      statusCode: 204,
      body: {},
    }).as("deleteUser");

    // Drivers
    cy.intercept("GET", "**/api/drivers", {
      statusCode: 200,
      body: [
        { driverId: "DRV0000001", name: "Saman Kumara", contact: "0771112223" },
      ],
    }).as("getDrivers");

    cy.intercept("POST", "**/api/drivers", {
      statusCode: 201,
      body: { driverId: "DRV0000002", name: "Upul Perera", contact: "0714445556" },
    }).as("createDriver");

    cy.intercept("PUT", "**/api/drivers/*", {
      statusCode: 200,
      body: { driverId: "DRV0000001", name: "Saman Kumara Perera", contact: "0771112223" },
    }).as("updateDriver");

    cy.intercept("DELETE", "**/api/drivers/*", {
      statusCode: 204,
      body: {},
    }).as("deleteDriver");

    // Vehicles
    cy.intercept("GET", "**/api/vehicles", {
      statusCode: 200,
      body: [
        { vehicleId: "VEH0000001", vehicleNo: "WP WP-1234", vehicleType: "Van" },
      ],
    }).as("getVehicles");

    cy.intercept("POST", "**/api/vehicles", {
      statusCode: 201,
      body: { vehicleId: "VEH0000002", vehicleNo: "WP LY-5678", vehicleType: "Lorry" },
    }).as("createVehicle");

    cy.intercept("PUT", "**/api/vehicles/*", {
      statusCode: 200,
      body: { vehicleId: "VEH0000001", vehicleNo: "WP WP-1234-Edit", vehicleType: "Van" },
    }).as("updateVehicle");

    cy.intercept("DELETE", "**/api/vehicles/*", {
      statusCode: 204,
      body: {},
    }).as("deleteVehicle");

    // Visit Login Page
    cy.visit("/login");

    // Fill credentials and click submit
    cy.get("#username").type("owner");
    cy.get("#password").type("owner1");
    cy.get('button[type="submit"]').click();

    // Confirm navigation to dashboard and then go to manage page
    cy.url().should("include", "/dashboard");
    cy.visit("/manage");
  });

  it("should load the Manage page with 5 tabs", () => {
    cy.contains("h1", "Manage Data").should("be.visible");
    cy.contains("button", "Products").should("be.visible");
    cy.contains("button", "Shops").should("be.visible");
    cy.contains("button", "Users").should("be.visible");
    cy.contains("button", "Drivers").should("be.visible");
    cy.contains("button", "Vehicles").should("be.visible");
  });

  it("should support CRUD actions for Products", () => {
    cy.contains("button", "Products").click();

    // Verify list loads
    cy.contains("Fish Bun").should("be.visible");
    cy.contains("White Bread").should("be.visible");

    // Add Product
    cy.get("#productName").type("Egg Bun");
    cy.contains("button", "Select category").click();
    cy.get('[role="option"]').contains("Buns").click();
    cy.get("#productUnitPrice").type("90.0");
    
    // Intercept GET response updates for mock refresh list
    cy.intercept("GET", "**/api/products", {
      statusCode: 200,
      body: [
        { proId: "PROD001", name: "Fish Bun", category: "Buns", unitPrice: 80.0, active: true },
        { proId: "PROD002", name: "White Bread", category: "Bread & Loaves", unitPrice: 120.0, active: true },
        { proId: "PROD003", name: "Egg Bun", category: "Buns", unitPrice: 90.0, active: true },
      ],
    }).as("getProductsUpdated");

    cy.contains("button", "Add Product").click();
    cy.wait("@createProduct");
    cy.contains("Egg Bun").should("be.visible");

    // Edit Product
    cy.get("tbody tr").first().find("button").eq(0).click(); // click Edit
    cy.get("#productName").clear().type("Fish Bun Special");
    cy.contains("button", "Update Product").click();
    cy.wait("@updateProduct");

    // Delete Product
    cy.stub(window, "confirm").returns(true);
    cy.get("tbody tr").first().find("button").eq(1).click(); // click Delete
    cy.wait("@deleteProduct");
  });

  it("should support CRUD actions for Shops", () => {
    cy.contains("button", "Shops").click();

    // Verify list loads
    cy.contains("Kodikara Grocery").should("be.visible");

    // Add Shop
    cy.get("#shopName").type("Silva Stores");
    cy.get("#shopOwnerName").type("Nimal");
    cy.get("#shopContactNo").type("0717654321");
    cy.get("#shopAddress").type("Matara");

    cy.intercept("GET", "**/api/shops", {
      statusCode: 200,
      body: [
        { shopId: "SHOP001", shopName: "Kodikara Grocery", ownerName: "Kamal", contactNo: "0771234567", address: "Galle" },
        { shopId: "SHOP002", shopName: "Silva Stores", ownerName: "Nimal", contactNo: "0717654321", address: "Matara" },
      ],
    }).as("getShopsUpdated");

    cy.contains("button", "Add Shop").click();
    cy.wait("@createShop");
    cy.contains("Silva Stores").should("be.visible");
  });

  it("should support CRUD actions for Users", () => {
    cy.contains("button", "Users").click();

    // Verify list loads
    cy.contains("salesman1").should("be.visible");

    // Add User (with the newly added Recovery Email field)
    cy.get("#salesmanUsername").type("salesman2");
    cy.get("#salesmanPassword").type("salesman2pass");
    cy.get("#salesmanRecoveryEmail").type("sales2@bakery.com");
    cy.get("#salesmanRole").click();
    cy.get('[role="option"]').contains("Salesman").click();

    cy.intercept("GET", "**/api/salesman/all", {
      statusCode: 200,
      body: [
        { userId: "USER000006", username: "salesman1", role: "ROLE_SALESMAN", recoveryEmail: "sales1@bakery.com" },
        { userId: "USER000007", username: "salesman2", role: "ROLE_SALESMAN", recoveryEmail: "sales2@bakery.com" },
      ],
    }).as("getUsersUpdated");

    cy.contains("button", "Add User").click();
    cy.wait("@createUser");
    cy.contains("salesman2").should("be.visible");
  });

  it("should support CRUD actions for Drivers", () => {
    cy.contains("button", "Drivers").click();

    // Verify list loads
    cy.contains("Saman Kumara").should("be.visible");

    // Add Driver
    cy.get("#driverName").type("Upul Perera");
    cy.get("#driverContact").type("0714445556");

    cy.intercept("GET", "**/api/drivers", {
      statusCode: 200,
      body: [
        { driverId: "DRV0000001", name: "Saman Kumara", contact: "0771112223" },
        { driverId: "DRV0000002", name: "Upul Perera", contact: "0714445556" },
      ],
    }).as("getDriversUpdated");

    cy.contains("button", "Add Driver").click();
    cy.wait("@createDriver");
    cy.contains("Upul Perera").should("be.visible");
  });

  it("should support CRUD actions for Vehicles", () => {
    cy.contains("button", "Vehicles").click();

    // Verify list loads
    cy.contains("WP WP-1234").should("be.visible");

    // Add Vehicle
    cy.get("#vehicleNo").type("WP LY-5678");
    cy.get("#vehicleType").click();
    cy.get('[role="option"]').contains("Lorry").click();

    cy.intercept("GET", "**/api/vehicles", {
      statusCode: 200,
      body: [
        { vehicleId: "VEH0000001", vehicleNo: "WP WP-1234", vehicleType: "Van" },
        { vehicleId: "VEH0000002", vehicleNo: "WP LY-5678", vehicleType: "Lorry" },
      ],
    }).as("getVehiclesUpdated");

    cy.contains("button", "Add Vehicle").click();
    cy.wait("@createVehicle");
    cy.contains("WP LY-5678").should("be.visible");
  });
});
