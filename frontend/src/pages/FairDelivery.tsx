import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ArrowLeft, Plus, Trash2, Edit2 } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { fairDeliveryService, FairDeliveryDTO, FairDeliveryItemDTO } from "@/services/fair-delivery.service";
import { productService, ProductDTO } from "@/services/product.service";
import { driverService, DriverDTO } from "@/services/driver.service";
import { vehicleService, VehicleDTO } from "@/services/vehicle.service";
import { format } from "date-fns";

interface ProductItem {
  id: string;
  productId: string;
  name: string;
  sentQuantity: number;
  price: number;
  returnedQuantity: number;
}

const FairDelivery = () => {
  const navigate = useNavigate();
  const { toast } = useToast();
  
  const [deliveries, setDeliveries] = useState<FairDeliveryDTO[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [editingDeliveryId, setEditingDeliveryId] = useState<string | null>(null);
  
  // Available options
  const [availableProducts, setAvailableProducts] = useState<ProductDTO[]>([]);
  const [availableDrivers, setAvailableDrivers] = useState<DriverDTO[]>([]);
  const [vehicleId, setVehicleId] = useState("");
  const [availableVehicles, setAvailableVehicles] = useState<VehicleDTO[]>([]);
  
  // Form states
  const [fairName, setFairName] = useState("");
  const [driverId, setDriverId] = useState("");
  const [status, setStatus] = useState("OUT");
  const [tax, setTax] = useState(0);
  const [extraPayments, setExtraPayments] = useState(0);
  const [dieselAmount, setDieselAmount] = useState(0);
  const [products, setProducts] = useState<ProductItem[]>([
    { id: "1", productId: "", name: "", sentQuantity: 0, price: 0, returnedQuantity: 0 }
  ]);

  // Load all data on mount
  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    await Promise.all([
      loadDeliveries(),
      loadProducts(),
      loadDrivers(),
      loadVehicles()
    ]);
  };

  const loadDeliveries = async () => {
    try {
      const data = await fairDeliveryService.list();
      setDeliveries(data);
    } catch (error: any) {
      console.error("Failed to load deliveries:", error);
    }
  };

  const loadProducts = async () => {
    try {
      const data = await productService.list();
      setAvailableProducts(data);
    } catch (error: any) {
      console.error("Failed to load products:", error);
      toast({
        title: "Error",
        description: "Failed to load products",
        variant: "destructive",
      });
    }
  };

  const loadDrivers = async () => {
    try {
      const data = await driverService.list();
      setAvailableDrivers(data);
    } catch (error: any) {
      console.error("Failed to load drivers:", error);
      toast({
        title: "Error",
        description: "Failed to load drivers",
        variant: "destructive",
      });
    }
  };
  const loadVehicles = async () => {
    try {
      const data = await vehicleService.list();
      setAvailableVehicles(data);
    } catch (error) {
      console.error("Failed to load vehicles", error);
    }
  };

  const addProduct = () => {
    setProducts([
      ...products,
      { id: Date.now().toString(), productId: "", name: "", sentQuantity: 0, price: 0, returnedQuantity: 0 }
    ]);
  };

  const updateProduct = (id: string, field: keyof ProductItem, value: string | number) => {
    setProducts(products.map(product => {
      if (product.id === id) {
        if (field === 'productId' && typeof value === 'string') {
          // When product is selected, auto-fill name and price
          const selectedProduct = availableProducts.find(p => p.proId === value);
          if (selectedProduct) {
            return {
              ...product,
              productId: value,
              name: selectedProduct.name,
              price: selectedProduct.unitPrice,
            };
          }
        }
        return { ...product, [field]: value };
      }
      return product;
    }));
  };

  const removeProduct = (id: string) => {
    if (products.length > 1) {
      setProducts(products.filter(product => product.id !== id));
    }
  };

  const validateForm = () => {
    if (!fairName.trim()) {
      toast({
        title: "Error",
        description: "Please enter fair name",
        variant: "destructive",
      });
      return false;
    }
    if (!vehicleId) {
      toast({
        title: "Error",
        description: "Please select a vehicle",
        variant: "destructive",
      });
      return false;
    }
    if (!driverId) {
      toast({
        title: "Error",
        description: "Please select a driver",
        variant: "destructive",
      });
      return false;
    }

    for (const product of products) {
      if (!product.productId) {
        toast({
          title: "Error",
          description: "Please select a product for all items",
          variant: "destructive",
        });
        return false;
      }
      if (product.sentQuantity <= 0) {
        toast({
          title: "Error",
          description: "Sent quantity must be greater than 0",
          variant: "destructive",
        });
        return false;
      }
    }

    return true;
  };

  const handleSave = async () => {
    if (!validateForm()) return;

    setIsLoading(true);
    try {
      const items: FairDeliveryItemDTO[] = products.map(p => ({
        productId: p.productId,
        qtySent: p.sentQuantity,
        unitPrice: p.price,
        qtyRemaining: p.returnedQuantity,
      }));

      const deliveryData: FairDeliveryDTO = {
        fairName,
        deliveryDate: format(new Date(), "yyyy-MM-dd"),
        driverId,
        vehicleId, 
        status,
        extraPayments,
        tax,
        dieselAmount,
        items,
      };

      let result;
      if (editingDeliveryId) {
        // Update existing delivery
        result = await fairDeliveryService.update(editingDeliveryId, deliveryData);
        toast({
          title: "Success!",
          description: "Fair delivery updated successfully",
        });
      } else {
        // Create new delivery
        result = await fairDeliveryService.create(deliveryData);
        toast({
          title: "Success!",
          description: "Fair delivery created successfully",
        });
      }

      // Reload deliveries
      await loadDeliveries();

      // Reset form
      resetForm();
    } catch (error: any) {
      console.error("Error saving delivery:", error);
      console.error("Error response:", error.response);
      const errorMessage = error.response?.data?.message || error.response?.data || error.message || "Failed to save delivery";
      toast({
        title: "Error",
        description: String(errorMessage),
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (delivery: FairDeliveryDTO) => {
    // Load delivery data into form
    setEditingDeliveryId(delivery.deliveryId!);
    setFairName(delivery.fairName);
    setDriverId(delivery.driverId);
    setVehicleId(delivery.vehicleId || "");
    setStatus(delivery.status);
    setTax(delivery.tax || 0);
    setExtraPayments(delivery.extraPayments || 0);
    setDieselAmount(delivery.dieselAmount || 0);

    // Load products
    if (delivery.items && delivery.items.length > 0) {
      const loadedProducts: ProductItem[] = delivery.items.map((item, index) => ({
        id: index.toString(),
        productId: item.productId,
        name: "", // Will be filled when product is selected
        sentQuantity: item.qtySent,
        price: item.unitPrice || 0,
        returnedQuantity: item.qtyRemaining || 0,
      }));
      setProducts(loadedProducts);
    }

    // Scroll to top to show form
    window.scrollTo({ top: 0, behavior: 'smooth' });

    toast({
      title: "Edit Mode",
      description: "Update the delivery details and click Save",
    });
  };

  const handleDelete = async (deliveryId: string) => {
    if (!confirm("Are you sure you want to delete this delivery? This action cannot be undone.")) {
      return;
    }

    setIsLoading(true);
    try {
      await fairDeliveryService.delete(deliveryId);
      toast({
        title: "Success!",
        description: "Fair delivery deleted successfully",
      });

      // Reload deliveries
      await loadDeliveries();
    } catch (error: any) {
      console.error("Error deleting delivery:", error);
      const errorMessage = error.response?.data?.message || error.response?.data || error.message || "Failed to delete delivery";
      toast({
        title: "Error",
        description: String(errorMessage),
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const resetForm = () => {
    setEditingDeliveryId(null);
    setFairName("");
    setDriverId("");
    setVehicleId("");
    setStatus("OUT");
    setTax(0);
    setExtraPayments(0);
    setDieselAmount(0);
    setProducts([{ id: "1", productId: "", name: "", sentQuantity: 0, price: 0, returnedQuantity: 0 }]);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-bakery-cream to-bakery-warm">
      <header className="bg-white/80 backdrop-blur-sm border-b border-border shadow-[var(--shadow-soft)]">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <Button variant="ghost" onClick={() => navigate("/dashboard")}>
              <ArrowLeft className="h-4 w-4" />
            </Button>
            <h1 className="text-xl font-bold text-bakery-brown">Fair Delivery Management</h1>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-4 py-8">
        {/* Add/Edit Form Window */}
        <div className="flex justify-center mb-8">
          <Card className="w-full max-w-2xl">
            <CardHeader>
              <CardTitle>{editingDeliveryId ? "Edit Fair Delivery" : "Add New Fair Delivery"}</CardTitle>
            </CardHeader>
            <CardContent className="space-y-6">
              {/* Fair Name */}
              <div>
                <Label htmlFor="fairName">Fair Name *</Label>
                <Input
                  id="fairName"
                  value={fairName}
                  onChange={(e) => setFairName(e.target.value)}
                  placeholder="Enter fair name"
                  disabled={isLoading}
                />
              </div>
               {/* Vehicle Selection */}
              <div>
                <Label htmlFor="vehicle">Vehicle * ({availableVehicles.length} available)</Label>
                <Select value={vehicleId} onValueChange={setVehicleId} disabled={isLoading}>
                  <SelectTrigger id="vehicle">
                    <SelectValue placeholder="Select vehicle" />
                  </SelectTrigger>
                  <SelectContent>
                    {availableVehicles.length === 0 ? (
                      <SelectItem value="none" disabled>No vehicles available</SelectItem>
                    ) : (
                      availableVehicles.map((v) => (
                        <SelectItem key={v.vehicleId} value={v.vehicleId!}>
                          {v.vehicleNo}
                        </SelectItem>
                      ))
                    )}
                  </SelectContent>
                </Select>
              </div>
              {/* Driver Selection */}
              <div>
                <Label htmlFor="driver">Driver * ({availableDrivers.length} available)</Label>
                <Select value={driverId} onValueChange={setDriverId} disabled={isLoading}>
                  <SelectTrigger id="driver">
                    <SelectValue placeholder="Select driver" />
                  </SelectTrigger>
                  <SelectContent>
                    {availableDrivers.length === 0 ? (
                      <SelectItem value="none" disabled>No drivers available</SelectItem>
                    ) : (
                      availableDrivers.map((d) => (
                        <SelectItem key={d.driverId} value={d.driverId!}>
                          {d.name}
                        </SelectItem>
                      ))
                    )}
                  </SelectContent>
                </Select>
              </div>

              {/* Status */}
              <div>
                <Label htmlFor="status">Status</Label>
                <Select value={status} onValueChange={setStatus} disabled={isLoading}>
                  <SelectTrigger id="status">
                    <SelectValue placeholder="Select status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="OUT">Out (Going to fair)</SelectItem>
                    <SelectItem value="RETURN">Return (Came back)</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              {/* Products Table */}
              <div>
                <div className="flex items-center justify-between mb-3">
                  <Label>Products * ({availableProducts.length} available)</Label>
                  <Button onClick={addProduct} size="sm" variant="outline" disabled={isLoading}>
                    <Plus className="h-4 w-4 mr-1" />
                    Add Product
                  </Button>
                </div>
                <div className="border rounded-lg overflow-hidden">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Product</TableHead>
                        <TableHead>Sent Qty</TableHead>
                        <TableHead>Price (Rs.)</TableHead>
                        <TableHead>Returned Qty</TableHead>
                        <TableHead className="w-[50px]"></TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {products.map((product) => (
                        <TableRow key={product.id}>
                          <TableCell>
                            <Select
                              value={product.productId}
                              onValueChange={(value) => updateProduct(product.id, "productId", value)}
                              disabled={isLoading}
                            >
                              <SelectTrigger className="h-9">
                                <SelectValue placeholder="Select" />
                              </SelectTrigger>
                              <SelectContent>
                                {availableProducts.length === 0 ? (
                                  <SelectItem value="none" disabled>No products</SelectItem>
                                ) : (
                                  availableProducts.map((p) => (
                                    <SelectItem key={p.proId} value={p.proId!}>
                                      {p.name} (Rs. {p.unitPrice})
                                    </SelectItem>
                                  ))
                                )}
                              </SelectContent>
                            </Select>
                          </TableCell>
                          <TableCell>
                            <Input
                              type="number"
                              min="0"
                              value={product.sentQuantity}
                              onChange={(e) => updateProduct(product.id, "sentQuantity", Math.max(0, Number(e.target.value)))}
                              placeholder="0"
                              className="h-9"
                              disabled={isLoading}
                            />
                          </TableCell>
                          <TableCell>
                            <Input
                              type="number"
                              min="0"
                              value={product.price}
                              readOnly
                              placeholder="Auto"
                              className="h-9 bg-muted"
                              disabled={isLoading}
                            />
                          </TableCell>
                          <TableCell>
                            <Input
                              type="number"
                              min="0"
                              value={product.returnedQuantity}
                              onChange={(e) => updateProduct(product.id, "returnedQuantity", Math.max(0, Number(e.target.value)))}
                              placeholder="0"
                              className="h-9"
                              disabled={isLoading}
                            />
                          </TableCell>
                          <TableCell>
                            {products.length > 1 && (
                              <Button
                                variant="ghost"
                                size="sm"
                                onClick={() => removeProduct(product.id)}
                                disabled={isLoading}
                              >
                                <Trash2 className="h-4 w-4 text-destructive" />
                              </Button>
                            )}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </div>
              </div>

              {/* Expenses */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div>
                  <Label htmlFor="tax">Tax (Rs.)</Label>
                  <Input
                    id="tax"
                    type="number"
                    min="0"
                    step="0.01"
                    value={tax}
                    onChange={(e) => setTax(Math.max(0, Number(e.target.value)))}
                    placeholder="0.00"
                    disabled={isLoading}
                  />
                </div>
                <div>
                  <Label htmlFor="extraPayments">Extra Payments (Rs.)</Label>
                  <Input
                    id="extraPayments"
                    type="number"
                    min="0"
                    step="0.01"
                    value={extraPayments}
                    onChange={(e) => setExtraPayments(Math.max(0, Number(e.target.value)))}
                    placeholder="0.00"
                    disabled={isLoading}
                  />
                </div>
                <div>
                  <Label htmlFor="dieselAmount">Diesel Amount (Rs.)</Label>
                  <Input
                    id="dieselAmount"
                    type="number"
                    min="0"
                    step="0.01"
                    value={dieselAmount}
                    onChange={(e) => setDieselAmount(Math.max(0, Number(e.target.value)))}
                    placeholder="0.00"
                    disabled={isLoading}
                  />
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex justify-end space-x-3">
                <Button variant="outline" onClick={resetForm} disabled={isLoading}>
                  {editingDeliveryId ? "Cancel Edit" : "Clear Form"}
                </Button>
                <Button onClick={handleSave} disabled={isLoading || availableProducts.length === 0 || availableDrivers.length === 0}>
                  {isLoading ? "Saving..." : editingDeliveryId ? "Update" : "Save"} Delivery
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Saved Deliveries List */}
        {deliveries.length > 0 && (
          <div className="space-y-4">
            <h2 className="text-xl font-semibold text-bakery-brown">Saved Deliveries ({deliveries.length})</h2>
            {deliveries.map((delivery) => (
              <Card key={delivery.deliveryId} className="overflow-hidden">
                <div className="p-4 bg-gradient-to-r from-primary/5 to-accent/5">
                  <div className="flex justify-between items-start">
                    <div className="space-y-2 flex-1">
                      <div className="flex items-center gap-4">
                        <h3 className="font-semibold text-lg">{delivery.fairName}</h3>
                        <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                          delivery.status === "OUT" 
                            ? "bg-amber-100 text-amber-700" 
                            : "bg-green-100 text-green-700"
                        }`}>
                          {delivery.status}
                        </span>
                      </div>
                      <p className="text-sm text-muted-foreground">Date: {delivery.deliveryDate}</p>
                      <p className="text-sm text-muted-foreground">ID: {delivery.deliveryId}</p>
                      
                      <div className="mt-3 space-y-2">
                        <p className="text-sm font-medium">Products:</p>
                        {delivery.items?.map((item, idx) => (
                          <div key={idx} className="pl-4 text-sm">
                            <p>
                              Product ID: {item.productId} - 
                              Sent: {item.qtySent}, 
                              Returned: {item.qtyRemaining || 0}, 
                              Price: Rs. {item.unitPrice}
                            </p>
                          </div>
                        ))}
                      </div>

                      <div className="mt-3 pt-3 border-t space-y-1">
                        <div className="grid grid-cols-2 gap-2 text-sm">
                          <p className="text-muted-foreground">Tax:</p>
                          <p className="font-medium">Rs. {delivery.tax}</p>
                          <p className="text-muted-foreground">Extra Payments:</p>
                          <p className="font-medium">Rs. {delivery.extraPayments}</p>
                          <p className="text-muted-foreground">Diesel Amount:</p>
                          <p className="font-medium">Rs. {delivery.dieselAmount}</p>
                          {delivery.profit !== undefined && (
                            <>
                              <p className="text-muted-foreground font-semibold">Profit:</p>
                              <p className={`font-bold ${delivery.profit >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                                Rs. {Number(delivery.profit).toFixed(2)}
                              </p>
                            </>
                          )}
                        </div>
                      </div>
                    </div>
                    
                    {/* Action Buttons */}
                    <div className="flex flex-col gap-2 ml-4">
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleEdit(delivery)}
                        disabled={isLoading}
                        className="flex items-center gap-2"
                      >
                        <Edit2 className="h-4 w-4" />
                        Edit
                      </Button>
                      <Button
                        size="sm"
                        variant="destructive"
                        onClick={() => handleDelete(delivery.deliveryId!)}
                        disabled={isLoading}
                        className="flex items-center gap-2"
                      >
                        <Trash2 className="h-4 w-4" />
                        Delete
                      </Button>
                    </div>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}
      </main>
    </div>
  );
};

export default FairDelivery;
