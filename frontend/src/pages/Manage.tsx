import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { ArrowLeft, Edit2, Trash2, Loader2, Check, ChevronsUpDown } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { productService, ProductDTO } from "@/services/product.service";
import { shopService, ShopDTO } from "@/services/shop.service";
import { salesmanService, SalesmanDTO } from "@/services/salesman.service";
import { driverService, DriverDTO } from "@/services/driver.service";
import { vehicleService, VehicleDTO } from "@/services/vehicle.service";
import { useAuth } from "@/contexts/AuthContext";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command";
import { cn } from "@/lib/utils";

const categories = [
  "Bread & Loaves",
  "Buns",
  "Short Eats",
  "Pastries",
  "Cakes & Swiss Rolls",
  "Rusks & Dry Bakery",
  "Biscuits & Cookies",
  "Packaged Snacks",
  "Beverages"
];

const Manage = () => {
  const navigate = useNavigate();
  const { toast } = useToast();
  const { userRole, loading, username } = useAuth();

  // Products State
  const [products, setProducts] = useState<ProductDTO[]>([]);
  const [productName, setProductName] = useState("");
  const [productCategory, setProductCategory] = useState("");
  const [productUnitPrice, setProductUnitPrice] = useState("");
  const [editingProductId, setEditingProductId] = useState<string | null>(null);
  const [isLoadingProducts, setIsLoadingProducts] = useState(false);
  const [openCategoryPopover, setOpenCategoryPopover] = useState(false);

  // Products Search State
  const [productSearchQuery, setProductSearchQuery] = useState("");
  const [productSearchField, setProductSearchField] = useState("name");

  const filteredProducts = products.filter((p) => {
    if (!productSearchQuery.trim()) return true;
    const query = productSearchQuery.toLowerCase().trim();
    if (productSearchField === "name") {
      return p.name.toLowerCase().includes(query);
    }
    if (productSearchField === "category") {
      return (p.category || "").toLowerCase().includes(query);
    }
    if (productSearchField === "price") {
      return p.unitPrice.toString().includes(query);
    }
    return true;
  });

  // Shops State
  const [shops, setShops] = useState<ShopDTO[]>([]);
  const [shopName, setShopName] = useState("");
  const [shopOwnerName, setShopOwnerName] = useState("");
  const [shopContactNo, setShopContactNo] = useState("");
  const [shopAddress, setShopAddress] = useState("");
  const [editingShopId, setEditingShopId] = useState<string | null>(null);
  const [isLoadingShops, setIsLoadingShops] = useState(false);

  // Shops Search State
  const [shopSearchQuery, setShopSearchQuery] = useState("");
  const [shopSearchField, setShopSearchField] = useState("name"); // default to Shop Name

  const filteredShops = shops.filter((s) => {
    if (!shopSearchQuery.trim()) return true;
    const query = shopSearchQuery.toLowerCase().trim();
    if (shopSearchField === "name") {
      return s.shopName.toLowerCase().includes(query);
    }
    if (shopSearchField === "owner") {
      return s.ownerName.toLowerCase().includes(query);
    }
    if (shopSearchField === "contact") {
      return s.contactNo.toLowerCase().includes(query);
    }
    if (shopSearchField === "location") {
      return s.address.toLowerCase().includes(query);
    }
    return true;
  });

  // Salesmen State
  const [salesmen, setSalesmen] = useState<SalesmanDTO[]>([]);
  const [salesmanUsername, setSalesmanUsername] = useState("");
  const [salesmanRole, setSalesmanRole] = useState("Salesman");
  const [salesmanPassword, setSalesmanPassword] = useState("");
  const [editingSalesmanId, setEditingSalesmanId] = useState<string | null>(null);
  const [isLoadingSalesmen, setIsLoadingSalesmen] = useState(false);

  const formatRole = (role: string) => {
    if (!role) return "";
    let cleanRole = role.replace(/^ROLE_/, "");
    if (cleanRole === "SALESMAN") return "Salesman";
    if (cleanRole === "OWNER") return "Owner";
    if (cleanRole === "DRIVER") return "Driver";
    return cleanRole.charAt(0).toUpperCase() + cleanRole.slice(1).toLowerCase();
  };

  // Drivers State
  const [drivers, setDrivers] = useState<DriverDTO[]>([]);
  const [driverName, setDriverName] = useState("");
  const [driverContact, setDriverContact] = useState("");
  const [editingDriverId, setEditingDriverId] = useState<string | null>(null);
  const [isLoadingDrivers, setIsLoadingDrivers] = useState(false);
  const [driverSearchQuery, setDriverSearchQuery] = useState("");
  const [driverSearchField, setDriverSearchField] = useState("name");

  const filteredDrivers = drivers.filter((d) => {
    if (!driverSearchQuery.trim()) return true;
    const query = driverSearchQuery.toLowerCase().trim();
    if (driverSearchField === "name") {
      return d.name.toLowerCase().includes(query);
    }
    if (driverSearchField === "contact") {
      return (d.contact || "").toLowerCase().includes(query);
    }
    return true;
  });

  // Vehicles State
  const [vehicles, setVehicles] = useState<VehicleDTO[]>([]);
  const [vehicleNo, setVehicleNo] = useState("");
  const [vehicleType, setVehicleType] = useState("Van");
  const [vehicleDriverName, setVehicleDriverName] = useState("Unassigned");
  const [editingVehicleId, setEditingVehicleId] = useState<string | null>(null);
  const [isLoadingVehicles, setIsLoadingVehicles] = useState(false);
  const [vehicleSearchQuery, setVehicleSearchQuery] = useState("");
  const [vehicleSearchField, setVehicleSearchField] = useState("no");

  const filteredVehicles = vehicles.filter((v) => {
    if (!vehicleSearchQuery.trim()) return true;
    const query = vehicleSearchQuery.toLowerCase().trim();
    if (vehicleSearchField === "no") {
      return v.vehicleNo.toLowerCase().includes(query);
    }
    if (vehicleSearchField === "type") {
      return (v.vehicleType || "").toLowerCase().includes(query);
    }
    if (vehicleSearchField === "driver") {
      return (v.driverName || "").toLowerCase().includes(query);
    }
    return true;
  });

  // Load data on mount
  useEffect(() => {
    if (!loading && !['ROLE_OWNER', 'ADMIN'].includes(userRole || '')) {
      navigate("/dashboard");
      return;
    }
    if (['ROLE_OWNER', 'ADMIN'].includes(userRole || '')) {
      loadProducts();
      loadShops();
      loadSalesmen();
      loadDrivers();
      loadVehicles();
    }
  }, [loading, userRole, navigate]);

  // ===== PRODUCT FUNCTIONS =====
  const loadProducts = async () => {
    setIsLoadingProducts(true);
    try {
      const data = await productService.list();
      setProducts(data);
    } catch (error: any) {
      toast({
        title: "Error Loading Products",
        description: error.response?.data?.message || "Failed to load products",
        variant: "destructive",
      });
    } finally {
      setIsLoadingProducts(false);
    }
  };

  const handleSaveProduct = async () => {
    if (!productName.trim() || !productCategory.trim() || !productUnitPrice) {
      toast({
        title: "Error",
        description: "Product name, category, and price are required",
        variant: "destructive",
      });
      return;
    }

    const price = parseFloat(productUnitPrice);
    if (isNaN(price) || price <= 0) {
      toast({
        title: "Error",
        description: "Price must be a valid number greater than 0",
        variant: "destructive",
      });
      return;
    }

    setIsLoadingProducts(true);
    try {
      const productData: ProductDTO = {
        name: productName,
        category: productCategory || undefined,
        unitPrice: price,
      };

      if (editingProductId) {
        await productService.update(editingProductId, productData);
        toast({ title: "Success", description: "Product updated successfully" });
      } else {
        await productService.create(productData);
        toast({ title: "Success", description: "Product added successfully" });
      }

      await loadProducts();
      setProductName("");
      setProductCategory("");
      setProductUnitPrice("");
      setEditingProductId(null);
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to save product",
        variant: "destructive",
      });
    } finally {
      setIsLoadingProducts(false);
    }
  };

  const handleEditProduct = (product: ProductDTO) => {
    setProductName(product.name);
    setProductCategory(product.category || "");
    setProductUnitPrice(product.unitPrice.toString());
    setEditingProductId(product.proId!);
  };

  const handleDeleteProduct = async (id: string) => {
    if (!confirm("Are you sure you want to delete this product?")) return;

    setIsLoadingProducts(true);
    try {
      await productService.delete(id);
      toast({ title: "Success", description: "Product deleted successfully" });
      await loadProducts();
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to delete product",
        variant: "destructive",
      });
    } finally {
      setIsLoadingProducts(false);
    }
  };

  // ===== SHOP FUNCTIONS =====
  const loadShops = async () => {
    setIsLoadingShops(true);
    try {
      const data = await shopService.list();
      setShops(data);
    } catch (error: any) {
      toast({
        title: "Error Loading Shops",
        description: error.response?.data?.message || "Failed to load shops",
        variant: "destructive",
      });
    } finally {
      setIsLoadingShops(false);
    }
  };

  const handleSaveShop = async () => {
    if (!shopName.trim() || !shopOwnerName.trim() || !shopContactNo.trim() || !shopAddress.trim()) {
      toast({
        title: "Error",
        description: "All fields are required",
        variant: "destructive",
      });
      return;
    }

    const contactRegex = /^\d{9,10}$/;
    if (!contactRegex.test(shopContactNo.trim())) {
      toast({
        title: "Error",
        description: "Contact number must be exactly 9 or 10 digits",
        variant: "destructive",
      });
      return;
    }

    setIsLoadingShops(true);
    try {
      const shopData: ShopDTO = {
        shopName,
        ownerName: shopOwnerName,
        contactNo: shopContactNo,
        address: shopAddress,
      };

      if (editingShopId) {
        await shopService.update(editingShopId, shopData);
        toast({ title: "Success", description: "Shop updated successfully" });
      } else {
        await shopService.create(shopData);
        toast({ title: "Success", description: "Shop added successfully" });
      }

      await loadShops();
      setShopName("");
      setShopOwnerName("");
      setShopContactNo("");
      setShopAddress("");
      setEditingShopId(null);
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to save shop",
        variant: "destructive",
      });
    } finally {
      setIsLoadingShops(false);
    }
  };

  const handleEditShop = (shop: ShopDTO) => {
    setShopName(shop.shopName);
    setShopOwnerName(shop.ownerName);
    setShopContactNo(shop.contactNo);
    setShopAddress(shop.address);
    setEditingShopId(shop.shopId!);
  };

  const handleDeleteShop = async (id: string) => {
    if (!confirm("Are you sure you want to delete this shop?")) return;

    setIsLoadingShops(true);
    try {
      await shopService.delete(id);
      toast({ title: "Success", description: "Shop deleted successfully" });
      await loadShops();
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to delete shop",
        variant: "destructive",
      });
    } finally {
      setIsLoadingShops(false);
    }
  };

  // ===== SALESMAN FUNCTIONS =====
  const loadSalesmen = async () => {
    setIsLoadingSalesmen(true);
    try {
      const data = await salesmanService.list();
      setSalesmen(data);
    } catch (error: any) {
      toast({
        title: "Error Loading Salesmen",
        description: error.response?.data?.message || "Failed to load salesmen",
        variant: "destructive",
      });
    } finally {
      setIsLoadingSalesmen(false);
    }
  };

  const handleSaveSalesman = async () => {
    if (!salesmanUsername.trim()) {
      toast({
        title: "Error",
        description: "Username is required",
        variant: "destructive",
      });
      return;
    }
    if (!editingSalesmanId && !salesmanPassword.trim()) {
      toast({ title: "Error", description: "Password is required", variant: "destructive" });
      return;
    }
    if (salesmanPassword && salesmanPassword.length < 6) {
      toast({ title: "Error", description: "Password must be at least 6 characters", variant: "destructive" });
      return;
    }
    if (!salesmanRole.trim()) {
      toast({
        title: "Error",
        description: "Role is required",
        variant: "destructive",
      });
      return;
    }

    setIsLoadingSalesmen(true);
    try {
      if (editingSalesmanId) {
        const updateData: any = { username: salesmanUsername, role: salesmanRole };
        if (salesmanPassword.trim()) {
          updateData.password = salesmanPassword;
        }
        await salesmanService.update(editingSalesmanId, updateData);
        toast({ title: "Success", description: "User updated successfully" });
      } else {
        await salesmanService.create({
          username: salesmanUsername,
          role: salesmanRole,
          password: salesmanPassword,
        });
        toast({ title: "Success", description: "User added successfully" });
      }

      await loadSalesmen();
      setSalesmanUsername("");
      setSalesmanPassword("");
      setSalesmanRole("Salesman");
      setEditingSalesmanId(null);
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to save user",
        variant: "destructive",
      });
    } finally {
      setIsLoadingSalesmen(false);
    }
  };

  const handleEditSalesman = (salesman: SalesmanDTO) => {
    setSalesmanUsername(salesman.username);

    // Normalize role to match dropdown Select options
    let cleanRole = salesman.role || "";
    if (cleanRole === "ROLE_SALESMAN") cleanRole = "Salesman";
    else if (cleanRole === "ROLE_OWNER" || cleanRole === "ADMIN") cleanRole = "Owner";
    else if (cleanRole === "ROLE_DRIVER") cleanRole = "Driver";

    setSalesmanRole(cleanRole);
    setEditingSalesmanId(salesman.userId!);
  };

  const handleDeleteSalesman = async (id: string) => {
    if (!confirm("Are you sure you want to delete this salesman?")) return;

    setIsLoadingSalesmen(true);
    try {
      await salesmanService.delete(id);
      toast({ title: "Success", description: "Salesman deleted successfully" });
      await loadSalesmen();
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to delete salesman",
        variant: "destructive",
      });
    } finally {
      setIsLoadingSalesmen(false);
    }
  };

  // ===== DRIVER FUNCTIONS =====
  const loadDrivers = async () => {
    setIsLoadingDrivers(true);
    try {
      const data = await driverService.list();
      setDrivers(data);
    } catch (error: any) {
      toast({
        title: "Error Loading Drivers",
        description: error.response?.data?.message || "Failed to load drivers",
        variant: "destructive",
      });
    } finally {
      setIsLoadingDrivers(false);
    }
  };

  const handleSaveDriver = async () => {
    if (!driverName.trim()) {
      toast({
        title: "Error",
        description: "Driver name is required",
        variant: "destructive",
      });
      return;
    }

    if (driverContact.trim()) {
      const contactRegex = /^\d{9,10}$/;
      if (!contactRegex.test(driverContact.trim())) {
        toast({
          title: "Error",
          description: "Contact number must be exactly 9 or 10 digits",
          variant: "destructive",
        });
        return;
      }
    }

    setIsLoadingDrivers(true);
    try {
      const payload = {
        name: driverName.trim(),
        contact: driverContact.trim() || undefined,
      };

      if (editingDriverId) {
        await driverService.update(editingDriverId, payload);
        toast({ title: "Success", description: "Driver updated successfully" });
      } else {
        await driverService.create(payload);
        toast({ title: "Success", description: "Driver added successfully" });
      }

      await loadDrivers();
      setDriverName("");
      setDriverContact("");
      setEditingDriverId(null);
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to save driver",
        variant: "destructive",
      });
    } finally {
      setIsLoadingDrivers(false);
    }
  };

  const handleEditDriver = (driver: DriverDTO) => {
    setDriverName(driver.name);
    setDriverContact(driver.contact || "");
    setEditingDriverId(driver.driverId);
  };

  const handleDeleteDriver = async (id: string) => {
    if (!confirm("Are you sure you want to delete this driver?")) return;

    setIsLoadingDrivers(true);
    try {
      await driverService.delete(id);
      toast({ title: "Success", description: "Driver deleted successfully" });
      await loadDrivers();
      await loadVehicles();
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to delete driver",
        variant: "destructive",
      });
    } finally {
      setIsLoadingDrivers(false);
    }
  };

  // ===== VEHICLE FUNCTIONS =====
  const loadVehicles = async () => {
    setIsLoadingVehicles(true);
    try {
      const data = await vehicleService.list();
      setVehicles(data);
    } catch (error: any) {
      toast({
        title: "Error Loading Vehicles",
        description: error.response?.data?.message || "Failed to load vehicles",
        variant: "destructive",
      });
    } finally {
      setIsLoadingVehicles(false);
    }
  };

  const handleSaveVehicle = async () => {
    if (!vehicleNo.trim() || !vehicleType.trim()) {
      toast({
        title: "Error",
        description: "Vehicle number and type are required",
        variant: "destructive",
      });
      return;
    }

    setIsLoadingVehicles(true);
    try {
      const payload = {
        vehicleNo: vehicleNo.trim(),
        vehicleType: vehicleType,
        driverName: vehicleDriverName === "Unassigned" || !vehicleDriverName ? undefined : vehicleDriverName,
      };

      if (editingVehicleId) {
        await vehicleService.update(editingVehicleId, payload);
        toast({ title: "Success", description: "Vehicle updated successfully" });
      } else {
        await vehicleService.create(payload);
        toast({ title: "Success", description: "Vehicle added successfully" });
      }

      await loadVehicles();
      setVehicleNo("");
      setVehicleType("Van");
      setVehicleDriverName("Unassigned");
      setEditingVehicleId(null);
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to save vehicle",
        variant: "destructive",
      });
    } finally {
      setIsLoadingVehicles(false);
    }
  };

  const handleEditVehicle = (vehicle: VehicleDTO) => {
    setVehicleNo(vehicle.vehicleNo);
    setVehicleType(vehicle.vehicleType || "Van");
    setVehicleDriverName(vehicle.driverName || "Unassigned");
    setEditingVehicleId(vehicle.vehicleId || null);
  };

  const handleDeleteVehicle = async (id: string) => {
    if (!confirm("Are you sure you want to delete this vehicle?")) return;

    setIsLoadingVehicles(true);
    try {
      await vehicleService.delete(id);
      toast({ title: "Success", description: "Vehicle deleted successfully" });
      await loadVehicles();
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to delete vehicle",
        variant: "destructive",
      });
    } finally {
      setIsLoadingVehicles(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-bakery-cream to-bakery-warm p-4">
      <div className="max-w-7xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center space-x-4">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => navigate("/dashboard")}
            >
              <ArrowLeft className="h-5 w-5" />
            </Button>
            <div>
              <h1 className="text-3xl font-bold text-bakery-brown">Manage Data</h1>
              <p className="text-muted-foreground">Add, edit, and manage products, shops, and users</p>
            </div>
          </div>
        </div>

        <Tabs defaultValue="products" className="w-full">
          <TabsList className="grid w-full grid-cols-5">
            <TabsTrigger value="products">Products</TabsTrigger>
            <TabsTrigger value="shops">Shops</TabsTrigger>
            <TabsTrigger value="salesmen">Users</TabsTrigger>
            <TabsTrigger value="drivers">Drivers</TabsTrigger>
            <TabsTrigger value="vehicles">Vehicles</TabsTrigger>
          </TabsList>

          {/* Products Tab */}
          <TabsContent value="products">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>{editingProductId ? "Edit Product" : "Add New Product"}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <Label htmlFor="productName">Product Name *</Label>
                    <Input
                      id="productName"
                      value={productName}
                      onChange={(e) => setProductName(e.target.value)}
                      placeholder="Product name"
                      disabled={isLoadingProducts}
                    />
                  </div>
                  <div className="flex flex-col space-y-2">
                    <Label htmlFor="productCategory">Category *</Label>
                    <Popover open={openCategoryPopover} onOpenChange={setOpenCategoryPopover}>
                      <PopoverTrigger asChild>
                        <Button
                          id="productCategory"
                          variant="outline"
                          role="combobox"
                          aria-expanded={openCategoryPopover}
                          className="w-full justify-between font-normal text-left"
                          disabled={isLoadingProducts}
                        >
                          {productCategory
                            ? categories.find((cat) => cat === productCategory) || productCategory
                            : "Select category"}
                          <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                        </Button>
                      </PopoverTrigger>
                      <PopoverContent className="w-[var(--radix-popover-trigger-width)] p-0" align="start">
                        <Command>
                          <CommandInput placeholder="Search category..." />
                          <CommandList>
                            <CommandEmpty>No category found.</CommandEmpty>
                            <CommandGroup>
                              {categories.map((cat) => (
                                <CommandItem
                                  key={cat}
                                  value={cat}
                                  onSelect={(currentValue) => {
                                    const matchedCat = categories.find(
                                      (c) => c.toLowerCase() === currentValue.toLowerCase()
                                    );
                                    setProductCategory(matchedCat || cat);
                                    setOpenCategoryPopover(false);
                                  }}
                                >
                                  <Check
                                    className={cn(
                                      "mr-2 h-4 w-4",
                                      productCategory === cat ? "opacity-100" : "opacity-0"
                                    )}
                                  />
                                  {cat}
                                </CommandItem>
                              ))}
                            </CommandGroup>
                          </CommandList>
                        </Command>
                      </PopoverContent>
                    </Popover>
                  </div>
                  <div>
                    <Label htmlFor="productUnitPrice">Unit Price (Rs.) *</Label>
                    <Input
                      id="productUnitPrice"
                      type="number"
                      min="0"
                      step="0.01"
                      value={productUnitPrice}
                      onChange={(e) => setProductUnitPrice(e.target.value)}
                      placeholder="0.00"
                      disabled={isLoadingProducts}
                    />
                  </div>
                  <div className="flex gap-2">
                    <Button onClick={handleSaveProduct} className="flex-1" disabled={isLoadingProducts}>
                      {isLoadingProducts && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                      {editingProductId ? "Update" : "Add"} Product
                    </Button>
                    {editingProductId && (
                      <Button
                        onClick={() => {
                          setProductName("");
                          setProductCategory("");
                          setProductUnitPrice("");
                          setEditingProductId(null);
                        }}
                        variant="outline"
                        disabled={isLoadingProducts}
                      >
                        Cancel
                      </Button>
                    )}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-2 sm:space-y-0 pb-4">
                  <CardTitle className="text-xl font-bold">Products List ({filteredProducts.length})</CardTitle>
                  <div className="flex items-center space-x-2 w-full sm:w-auto">
                    <Input
                      placeholder="Search..."
                      value={productSearchQuery}
                      onChange={(e) => setProductSearchQuery(e.target.value)}
                      className="h-9 w-full sm:w-[240px]"
                    />
                    <Select value={productSearchField} onValueChange={setProductSearchField}>
                      <SelectTrigger className="h-9 w-[110px]">
                        <SelectValue placeholder="Search by" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="name">Name</SelectItem>
                        <SelectItem value="category">Category</SelectItem>
                        <SelectItem value="price">Price</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </CardHeader>
                <CardContent>
                  {isLoadingProducts ? (
                    <div className="text-center py-8">
                      <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
                      <p className="text-muted-foreground mt-2">Loading products...</p>
                    </div>
                  ) : products.length === 0 ? (
                    <p className="text-muted-foreground text-center py-8">No products added yet</p>
                  ) : (
                    <div className="overflow-auto max-h-[500px]">
                      <Table>
                        <TableHeader>
                          <TableRow>
                            <TableHead>Name</TableHead>
                            <TableHead>Category</TableHead>
                            <TableHead>Price</TableHead>
                            <TableHead>Actions</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {filteredProducts.map((product) => (
                            <TableRow key={product.proId}>
                              <TableCell>{product.name}</TableCell>
                              <TableCell>{product.category || "-"}</TableCell>
                              <TableCell>Rs. {product.unitPrice}</TableCell>
                              <TableCell>
                                <div className="flex gap-2">
                                  <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => handleEditProduct(product)}
                                    disabled={isLoadingProducts}
                                  >
                                    <Edit2 className="h-4 w-4" />
                                  </Button>
                                  <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => handleDeleteProduct(product.proId!)}
                                    disabled={isLoadingProducts}
                                  >
                                    <Trash2 className="h-4 w-4 text-destructive" />
                                  </Button>
                                </div>
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          {/* Shops Tab */}
          <TabsContent value="shops">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>{editingShopId ? "Edit Shop" : "Add New Shop"}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <Label htmlFor="shopName">Shop Name *</Label>
                    <Input
                      id="shopName"
                      value={shopName}
                      onChange={(e) => setShopName(e.target.value)}
                      placeholder="Shop name"
                      disabled={isLoadingShops}
                    />
                  </div>
                  <div>
                    <Label htmlFor="shopOwnerName">Owner Name *</Label>
                    <Input
                      id="shopOwnerName"
                      value={shopOwnerName}
                      onChange={(e) => setShopOwnerName(e.target.value)}
                      placeholder="Owner name"
                      disabled={isLoadingShops}
                    />
                  </div>
                  <div>
                    <Label htmlFor="shopContactNo">Contact Number *</Label>
                    <Input
                      id="shopContactNo"
                      value={shopContactNo}
                      onChange={(e) => setShopContactNo(e.target.value)}
                      placeholder="Contact number"
                      disabled={isLoadingShops}
                    />
                  </div>
                  <div>
                    <Label htmlFor="shopAddress">Address/Location *</Label>
                    <Input
                      id="shopAddress"
                      value={shopAddress}
                      onChange={(e) => setShopAddress(e.target.value)}
                      placeholder="Location"
                      disabled={isLoadingShops}
                    />
                  </div>
                  <div className="flex gap-2">
                    <Button onClick={handleSaveShop} className="flex-1" disabled={isLoadingShops}>
                      {isLoadingShops && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                      {editingShopId ? "Update" : "Add"} Shop
                    </Button>
                    {editingShopId && (
                      <Button
                        onClick={() => {
                          setShopName("");
                          setShopOwnerName("");
                          setShopContactNo("");
                          setShopAddress("");
                          setEditingShopId(null);
                        }}
                        variant="outline"
                        disabled={isLoadingShops}
                      >
                        Cancel
                      </Button>
                    )}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-2 sm:space-y-0 pb-4">
                  <CardTitle className="text-xl font-bold">Shops List ({filteredShops.length})</CardTitle>
                  <div className="flex items-center space-x-2 w-full sm:w-auto">
                    <Input
                      placeholder="Search..."
                      value={shopSearchQuery}
                      onChange={(e) => setShopSearchQuery(e.target.value)}
                      className="h-9 w-full sm:w-[240px]"
                    />
                    <Select value={shopSearchField} onValueChange={setShopSearchField}>
                      <SelectTrigger className="h-9 w-[110px]">
                        <SelectValue placeholder="Search by" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="name">Shop Name</SelectItem>
                        <SelectItem value="owner">Owner</SelectItem>
                        <SelectItem value="contact">Contact</SelectItem>
                        <SelectItem value="location">Location</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </CardHeader>
                <CardContent>
                  {isLoadingShops ? (
                    <div className="text-center py-8">
                      <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
                      <p className="text-muted-foreground mt-2">Loading shops...</p>
                    </div>
                  ) : shops.length === 0 ? (
                    <p className="text-muted-foreground text-center py-8">No shops added yet</p>
                  ) : (
                    <div className="overflow-auto max-h-[500px]">
                      <Table>
                        <TableHeader>
                          <TableRow>
                            <TableHead>Shop Name</TableHead>
                            <TableHead>Owner</TableHead>
                            <TableHead>Contact</TableHead>
                            <TableHead>Location</TableHead>
                            <TableHead>Actions</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {filteredShops.map((shop) => (
                            <TableRow key={shop.shopId}>
                              <TableCell>{shop.shopName}</TableCell>
                              <TableCell>{shop.ownerName}</TableCell>
                              <TableCell>{shop.contactNo}</TableCell>
                              <TableCell>{shop.address}</TableCell>
                              <TableCell>
                                <div className="flex gap-2">
                                  <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => handleEditShop(shop)}
                                    disabled={isLoadingShops}
                                  >
                                    <Edit2 className="h-4 w-4" />
                                  </Button>
                                  <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => handleDeleteShop(shop.shopId!)}
                                    disabled={isLoadingShops}
                                  >
                                    <Trash2 className="h-4 w-4 text-destructive" />
                                  </Button>
                                </div>
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          {/* Users Tab */}
          <TabsContent value="salesmen">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>{editingSalesmanId ? "Edit User" : "Add New User"}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <Label htmlFor="salesmanUsername">Username *</Label>
                    <Input
                      id="salesmanUsername"
                      value={salesmanUsername}
                      onChange={(e) => setSalesmanUsername(e.target.value)}
                      placeholder="Username"
                      disabled={isLoadingSalesmen}
                    />
                  </div>
                  <div>
                    <Label htmlFor="salesmanPassword">
                      Password {editingSalesmanId ? "(Leave blank to keep current)" : "*"}</Label>
                    <Input
                      id="salesmanPassword"
                      type="password"
                      value={salesmanPassword}
                      onChange={(e) => setSalesmanPassword(e.target.value)}
                      placeholder="Password"
                      disabled={isLoadingSalesmen}
                    />
                  </div>
                  <div>
                    <Label htmlFor="salesmanRole">Role *</Label>
                    <Select
                      value={salesmanRole}
                      onValueChange={setSalesmanRole}
                      disabled={isLoadingSalesmen}
                    >
                      <SelectTrigger id="salesmanRole">
                        <SelectValue placeholder="Select role" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Salesman">Salesman</SelectItem>
                        <SelectItem value="Driver">Driver</SelectItem>
                        <SelectItem value="Owner">Owner</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="text-sm text-muted-foreground">
                    Default password will be same as username
                  </div>
                  <div className="flex gap-2">
                    <Button onClick={handleSaveSalesman} className="flex-1" disabled={isLoadingSalesmen}>
                      {isLoadingSalesmen && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                      {editingSalesmanId ? "Update" : "Add"} User
                    </Button>
                    {editingSalesmanId && (
                      <Button
                        onClick={() => {
                          setSalesmanUsername("");
                          setSalesmanRole("Salesman");
                          setSalesmanPassword("");
                          setEditingSalesmanId(null);
                        }}
                        variant="outline"
                        disabled={isLoadingSalesmen}
                      >
                        Cancel
                      </Button>
                    )}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Users List ({(salesmen || []).filter((s) => s.username !== username).length})</CardTitle>
                </CardHeader>
                <CardContent>
                  {isLoadingSalesmen ? (
                    <div className="text-center py-8">
                      <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
                      <p className="text-muted-foreground mt-2">Loading users...</p>
                    </div>
                  ) : (salesmen || []).filter((s) => s.username !== username).length === 0 ? (
                    <p className="text-muted-foreground text-center py-8">No users added yet</p>
                  ) : (
                    <div className="overflow-auto max-h-[500px]">
                      <Table>
                        <TableHeader>
                          <TableRow>
                            <TableHead>Username</TableHead>
                            <TableHead>User ID</TableHead>
                            <TableHead>Role</TableHead>
                            <TableHead>Actions</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {(salesmen || [])
                            .filter((s) => s.username !== username)
                            .map((salesman) => (
                              <TableRow key={salesman.userId}>
                                <TableCell>{salesman.username}</TableCell>
                                <TableCell>{salesman.userId}</TableCell>
                                <TableCell>{formatRole(salesman.role)}</TableCell>
                                <TableCell>
                                  <div className="flex gap-2">
                                    <Button
                                      size="sm"
                                      variant="ghost"
                                      onClick={() => handleEditSalesman(salesman)}
                                      disabled={isLoadingSalesmen}
                                    >
                                      <Edit2 className="h-4 w-4" />
                                    </Button>
                                    <Button
                                      size="sm"
                                      variant="ghost"
                                      onClick={() => handleDeleteSalesman(salesman.userId!)}
                                      disabled={isLoadingSalesmen}
                                    >
                                      <Trash2 className="h-4 w-4 text-destructive" />
                                    </Button>
                                  </div>
                                </TableCell>
                              </TableRow>
                            ))}
                        </TableBody>
                      </Table>
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          {/* Drivers Tab */}
          <TabsContent value="drivers">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>{editingDriverId ? "Edit Driver" : "Add New Driver"}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <Label htmlFor="driverName">Driver Name *</Label>
                    <Input
                      id="driverName"
                      value={driverName}
                      onChange={(e) => setDriverName(e.target.value)}
                      placeholder="Driver name"
                      disabled={isLoadingDrivers}
                    />
                  </div>
                  <div>
                    <Label htmlFor="driverContact">Contact Number</Label>
                    <Input
                      id="driverContact"
                      value={driverContact}
                      onChange={(e) => setDriverContact(e.target.value)}
                      placeholder="Contact number (e.g. 0771234567)"
                      disabled={isLoadingDrivers}
                    />
                  </div>
                  <div className="flex gap-2">
                    <Button onClick={handleSaveDriver} className="flex-1" disabled={isLoadingDrivers}>
                      {isLoadingDrivers && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                      {editingDriverId ? "Update" : "Add"} Driver
                    </Button>
                    {editingDriverId && (
                      <Button
                        onClick={() => {
                          setDriverName("");
                          setDriverContact("");
                          setEditingDriverId(null);
                        }}
                        variant="outline"
                        disabled={isLoadingDrivers}
                      >
                        Cancel
                      </Button>
                    )}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-2 sm:space-y-0 pb-4">
                  <CardTitle className="text-xl font-bold">Drivers List ({filteredDrivers.length})</CardTitle>
                  <div className="flex items-center space-x-2 w-full sm:w-auto">
                    <Input
                      placeholder="Search..."
                      value={driverSearchQuery}
                      onChange={(e) => setDriverSearchQuery(e.target.value)}
                      className="h-9 w-full sm:w-[200px]"
                    />
                    <Select value={driverSearchField} onValueChange={setDriverSearchField}>
                      <SelectTrigger className="h-9 w-[110px]">
                        <SelectValue placeholder="Search by" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="name">Name</SelectItem>
                        <SelectItem value="contact">Contact</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </CardHeader>
                <CardContent>
                  {isLoadingDrivers ? (
                    <div className="text-center py-8">
                      <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
                      <p className="text-muted-foreground mt-2">Loading drivers...</p>
                    </div>
                  ) : drivers.length === 0 ? (
                    <p className="text-muted-foreground text-center py-8">No drivers added yet</p>
                  ) : (
                    <div className="overflow-auto max-h-[500px]">
                      <Table>
                        <TableHeader>
                          <TableRow>
                            <TableHead>Driver ID</TableHead>
                            <TableHead>Name</TableHead>
                            <TableHead>Contact</TableHead>
                            <TableHead>Actions</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {filteredDrivers.map((driver) => (
                            <TableRow key={driver.driverId}>
                              <TableCell>{driver.driverId}</TableCell>
                              <TableCell>{driver.name}</TableCell>
                              <TableCell>{driver.contact || "-"}</TableCell>
                              <TableCell>
                                <div className="flex gap-2">
                                  <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => handleEditDriver(driver)}
                                    disabled={isLoadingDrivers}
                                  >
                                    <Edit2 className="h-4 w-4" />
                                  </Button>
                                  <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => handleDeleteDriver(driver.driverId!)}
                                    disabled={isLoadingDrivers}
                                  >
                                    <Trash2 className="h-4 w-4 text-destructive" />
                                  </Button>
                                </div>
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          {/* Vehicles Tab */}
          <TabsContent value="vehicles">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>{editingVehicleId ? "Edit Vehicle" : "Add New Vehicle"}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <Label htmlFor="vehicleNo">Vehicle Number *</Label>
                    <Input
                      id="vehicleNo"
                      value={vehicleNo}
                      onChange={(e) => setVehicleNo(e.target.value)}
                      placeholder="e.g. WP WP-1234 or WP-PC-1234"
                      disabled={isLoadingVehicles}
                    />
                  </div>
                  <div>
                    <Label htmlFor="vehicleType">Vehicle Type *</Label>
                    <Select
                      value={vehicleType}
                      onValueChange={setVehicleType}
                      disabled={isLoadingVehicles}
                    >
                      <SelectTrigger id="vehicleType">
                        <SelectValue placeholder="Select type" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Three-Wheel">Three-Wheel</SelectItem>
                        <SelectItem value="Lorry">Lorry</SelectItem>
                        <SelectItem value="Van">Van</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div>
                    <Label htmlFor="vehicleDriverName">Assigned Driver</Label>
                    <Select
                      value={vehicleDriverName}
                      onValueChange={setVehicleDriverName}
                      disabled={isLoadingVehicles}
                    >
                      <SelectTrigger id="vehicleDriverName">
                        <SelectValue placeholder="Select driver" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="Unassigned">Unassigned (None)</SelectItem>
                        {drivers.map((d) => (
                          <SelectItem key={d.driverId} value={d.name}>
                            {d.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="flex gap-2">
                    <Button onClick={handleSaveVehicle} className="flex-1" disabled={isLoadingVehicles}>
                      {isLoadingVehicles && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                      {editingVehicleId ? "Update" : "Add"} Vehicle
                    </Button>
                    {editingVehicleId && (
                      <Button
                        onClick={() => {
                          setVehicleNo("");
                          setVehicleType("Van");
                          setVehicleDriverName("Unassigned");
                          setEditingVehicleId(null);
                        }}
                        variant="outline"
                        disabled={isLoadingVehicles}
                      >
                        Cancel
                      </Button>
                    )}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-2 sm:space-y-0 pb-4">
                  <CardTitle className="text-xl font-bold">Vehicles List ({filteredVehicles.length})</CardTitle>
                  <div className="flex items-center space-x-2 w-full sm:w-auto">
                    <Input
                      placeholder="Search..."
                      value={vehicleSearchQuery}
                      onChange={(e) => setVehicleSearchQuery(e.target.value)}
                      className="h-9 w-full sm:w-[200px]"
                    />
                    <Select value={vehicleSearchField} onValueChange={setVehicleSearchField}>
                      <SelectTrigger className="h-9 w-[110px]">
                        <SelectValue placeholder="Search by" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="no">Vehicle No</SelectItem>
                        <SelectItem value="type">Type</SelectItem>
                        <SelectItem value="driver">Driver</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </CardHeader>
                <CardContent>
                  {isLoadingVehicles ? (
                    <div className="text-center py-8">
                      <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
                      <p className="text-muted-foreground mt-2">Loading vehicles...</p>
                    </div>
                  ) : vehicles.length === 0 ? (
                    <p className="text-muted-foreground text-center py-8">No vehicles added yet</p>
                  ) : (
                    <div className="overflow-auto max-h-[500px]">
                      <Table>
                        <TableHeader>
                          <TableRow>
                            <TableHead>Vehicle ID</TableHead>
                            <TableHead>Vehicle No</TableHead>
                            <TableHead>Type</TableHead>
                            <TableHead>Driver</TableHead>
                            <TableHead>Actions</TableHead>
                          </TableRow>
                        </TableHeader>
                        <TableBody>
                          {filteredVehicles.map((vehicle) => (
                            <TableRow key={vehicle.vehicleId}>
                              <TableCell>{vehicle.vehicleId}</TableCell>
                              <TableCell>{vehicle.vehicleNo}</TableCell>
                              <TableCell>{vehicle.vehicleType || "-"}</TableCell>
                              <TableCell>{vehicle.driverName || "Unassigned"}</TableCell>
                              <TableCell>
                                <div className="flex gap-2">
                                  <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => handleEditVehicle(vehicle)}
                                    disabled={isLoadingVehicles}
                                  >
                                    <Edit2 className="h-4 w-4" />
                                  </Button>
                                  <Button
                                    size="sm"
                                    variant="ghost"
                                    onClick={() => handleDeleteVehicle(vehicle.vehicleId!)}
                                    disabled={isLoadingVehicles}
                                  >
                                    <Trash2 className="h-4 w-4 text-destructive" />
                                  </Button>
                                </div>
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default Manage;
