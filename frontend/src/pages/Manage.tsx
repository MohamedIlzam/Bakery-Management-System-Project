import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { ArrowLeft, Edit2, Trash2, Loader2 } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { productService, ProductDTO } from "@/services/product.service";
import { shopService, ShopDTO } from "@/services/shop.service";
import { salesmanService, SalesmanDTO } from "@/services/salesman.service";

const Manage = () => {
  const navigate = useNavigate();
  const { toast } = useToast();

  // Products State
  const [products, setProducts] = useState<ProductDTO[]>([]);
  const [productName, setProductName] = useState("");
  const [productCategory, setProductCategory] = useState("");
  const [productUnitPrice, setProductUnitPrice] = useState("");
  const [editingProductId, setEditingProductId] = useState<string | null>(null);
  const [isLoadingProducts, setIsLoadingProducts] = useState(false);

  // Shops State
  const [shops, setShops] = useState<ShopDTO[]>([]);
  const [shopName, setShopName] = useState("");
  const [shopOwnerName, setShopOwnerName] = useState("");
  const [shopContactNo, setShopContactNo] = useState("");
  const [shopAddress, setShopAddress] = useState("");
  const [editingShopId, setEditingShopId] = useState<string | null>(null);
  const [isLoadingShops, setIsLoadingShops] = useState(false);

  // Salesmen State
  const [salesmen, setSalesmen] = useState<SalesmanDTO[]>([]);
  const [salesmanUsername, setSalesmanUsername] = useState("");
  const [salesmanRole, setSalesmanRole] = useState("ROLE_SALESMAN");
  const [salesmanPassword, setSalesmanPassword] = useState("");
  const [editingSalesmanId, setEditingSalesmanId] = useState<string | null>(null);
  const [isLoadingSalesmen, setIsLoadingSalesmen] = useState(false);

  // Load data on mount
  useEffect(() => {
    loadProducts();
    loadShops();
    loadSalesmen();
  }, []);

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
    if (!productName.trim() || !productUnitPrice) {
      toast({
        title: "Error",
        description: "Product name and price are required",
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
      setSalesmanRole("ROLE_SALESMAN");
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
    setSalesmanRole(salesman.role);
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
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="products">Products</TabsTrigger>
            <TabsTrigger value="shops">Shops</TabsTrigger>
            <TabsTrigger value="salesmen">Users</TabsTrigger>
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
                  <div>
                    <Label htmlFor="productCategory">Category</Label>
                    <Input
                      id="productCategory"
                      value={productCategory}
                      onChange={(e) => setProductCategory(e.target.value)}
                      placeholder="Category"
                      disabled={isLoadingProducts}
                    />
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
                <CardHeader>
                  <CardTitle>Products List ({products.length})</CardTitle>
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
                          {products.map((product) => (
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
                <CardHeader>
                  <CardTitle>Shops List ({shops.length})</CardTitle>
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
                          {shops.map((shop) => (
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
                        <SelectItem value="ROLE_SALESMAN">Salesman</SelectItem>
                        <SelectItem value="ROLE_DRIVER">Driver</SelectItem>
                        <SelectItem value="ROLE_OWNER">Owner</SelectItem>
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
                          setSalesmanRole("ROLE_SALESMAN");
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
                  <CardTitle>Users List ({salesmen.length})</CardTitle>
                </CardHeader>
                <CardContent>
                  {isLoadingSalesmen ? (
                    <div className="text-center py-8">
                      <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
                      <p className="text-muted-foreground mt-2">Loading users...</p>
                    </div>
                  ) : salesmen.length === 0 ? (
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
                          {salesmen.map((salesman) => (
                            <TableRow key={salesman.userId}>
                              <TableCell>{salesman.username}</TableCell>
                              <TableCell>{salesman.userId}</TableCell>
                              <TableCell>{salesman.role}</TableCell>
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
        </Tabs>
      </div>
    </div>
  );
};

export default Manage;
