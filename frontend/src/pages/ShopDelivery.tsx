// import { useState, useEffect } from "react";
// import { useNavigate } from "react-router-dom";
// import { Button } from "@/components/ui/button";
// import { Input } from "@/components/ui/input";
// import { Label } from "@/components/ui/label";
// import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
// import { ArrowLeft, Plus, Trash2, Edit2, Printer } from "lucide-react";
// import { useToast } from "@/hooks/use-toast";
// import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
// import { shopSupplyService, ShopSupplyRequestDTO, ShopSupplyItemDTO, ShopSupplyResponseDTO } from "@/services/shop-supply.service";
// import { productService, ProductDTO } from "@/services/product.service";
// import { shopService, ShopDTO } from "@/services/shop.service";
// import { driverService, DriverDTO } from "@/services/driver.service";
// import { vehicleService, VehicleDTO } from "@/services/vehicle.service";
// // FIX 1: Correct Import with curly braces
// import { printBill } from "../utils/printBill";

// interface ProductItem {
//   id: string;
//   productId: string;
//   productName: string;
//   quantity: number;
//   price: number;
// }

// const ShopDelivery = () => {
//   const navigate = useNavigate();
//   const { toast } = useToast();
  
//   // FIX 2: Better User Handling (Mock or Real)
//   const getCurrentUser = () => {
//     const userStr = localStorage.getItem('user');
//     // FAILSAFE: If no user in local storage, use a hardcoded fallback for testing
//     // REPLACE "USR001" with a valid User ID from your database!
//     return userStr ? JSON.parse(userStr) : { userId: "USR001", role: "OWNER" };
//   };
  
//   const currentUser = getCurrentUser();
//   const isAdmin = currentUser?.role === 'OWNER';
  
//   const [shopId, setShopId] = useState("");
//   const [driverId, setDriverId] = useState(""); 
//   const [products, setProducts] = useState<ProductItem[]>([
//     { id: "1", productId: "", productName: "", quantity: 0, price: 0 }
//   ]);
  
//   const [availableProducts, setAvailableProducts] = useState<ProductDTO[]>([]);
//   const [availableShops, setAvailableShops] = useState<ShopDTO[]>([]);
//   const [availableDrivers, setAvailableDrivers] = useState<any[]>([]);
//   const [availableVehicles, setAvailableVehicles] = useState<VehicleDTO[]>([]);
//   const [vehicleId, setVehicleId] = useState("");
//   const [savedDeliveries, setSavedDeliveries] = useState<ShopSupplyResponseDTO[]>([]);
//   const [editingDeliveryId, setEditingDeliveryId] = useState<string | null>(null);
//   const [isLoading, setIsLoading] = useState(false);

//   useEffect(() => {
//     loadAllData();
//   }, []);

//   const loadAllData = async () => {
//     await Promise.all([
//       loadProducts(),
//       loadShops(),
//       loadDrivers(),
//       loadVehicles(),
//       loadDeliveries()
//     ]);
//   };

//   const loadProducts = async () => {
//     try {
//       const data = await productService.list();
//       setAvailableProducts(data);
//     } catch (error: any) { console.error("Failed to load products"); }
//   };

//   const loadShops = async () => {
//     try {
//       const data = await shopService.list();
//       setAvailableShops(data);
//     } catch (error: any) { console.error("Failed to load shops"); }
//   };

//   const loadDrivers = async () => {
//     try {
//       const data = await driverService.list();
//       setAvailableDrivers(data);
//     } catch (error: any) { console.error("Failed to load drivers"); }
//   };
  
//   const loadVehicles = async () => {
//     try {
//       const data = await vehicleService.list();
//       setAvailableVehicles(data);
//     } catch (error: any) { console.error("Failed to load vehicles"); }
//   };
  
//   const loadDeliveries = async () => {
//     try {
//       const data = await shopSupplyService.list();
//       setSavedDeliveries(data);
//     } catch (error: any) { console.error("Failed to load deliveries"); }
//   };

//   const addProduct = () => {
//     setProducts([...products, { id: Date.now().toString(), productId: "", productName: "", quantity: 0, price: 0 }]);
//   };

//   const updateProduct = (id: string, field: keyof ProductItem, value: any) => {
//     setProducts(products.map(product => {
//       if (product.id === id) {
//         if (field === 'productId' && typeof value === 'string') {
//           const selectedProduct = availableProducts.find(p => p.proId === value);
//           if (selectedProduct) {
//             return { ...product, productId: value, productName: selectedProduct.name, price: selectedProduct.unitPrice };
//           }
//         }
//         return { ...product, [field]: value };
//       }
//       return product;
//     }));
//   };

//   const removeProduct = (id: string) => {
//     if (products.length > 1) {
//       setProducts(products.filter(product => product.id !== id));
//     }
//   };

//   const handleSave = async () => {
//     if (!shopId) {
//       toast({ title: "Error", description: "Please select a shop", variant: "destructive" });
//       return;
//     }
//     // Driver is now optional at assignment stage, but vehicle is needed
//     if (!vehicleId) {
//       toast({ title: "Error", description: "Please select a vehicle", variant: "destructive" });
//       return;
//     }

//     // Filter valid items
//     const validItems = products.filter(p => p.productId && p.quantity > 0);
    
//     // Assignment Logic: Allow saving if empty, but confirm
//     if (validItems.length === 0 && !confirm("Save as Assignment (No products)?")) {
//         return;
//     }

//     setIsLoading(true);
//     try {
//       const items: ShopSupplyItemDTO[] = validItems.map(p => ({
//         productId: p.productId,
//         productName: p.productName,
//         quantity: p.quantity,
//         price: p.price,
//         shopId: shopId,
//       }));

//       // FIX 3: Correct Mapping for Backend
//       const requestBody: ShopSupplyRequestDTO = {
//         shopId,
//         salesmanId: currentUser.userId, // Send USER ID (The 404 Fix!)
//         driverId: driverId,             // Send DRIVER ID (Separately)
//         vehicleId,
//         items,
//       };

//       if (editingDeliveryId) {
//         await shopSupplyService.update(editingDeliveryId, requestBody);
//         toast({ title: "Success", description: "Updated successfully" });
//       } else {
//         await shopSupplyService.create(requestBody);
//         toast({ title: "Success", description: "Saved successfully" });
//       }

//       await loadDeliveries();
//       resetForm();
//     } catch (error: any) {
//       console.error("Error saving:", error);
//       const msg = error.response?.data?.message || "Failed to save.";
//       toast({ title: "Error", description: msg, variant: "destructive" });
//     } finally {
//       setIsLoading(false);
//     }
//   };

//   const handleEdit = (delivery: ShopSupplyResponseDTO) => {
//     setEditingDeliveryId(delivery.supplyId);
//     setShopId(delivery.shopId || "");
//     setDriverId(delivery.driverId || ""); 
//     setVehicleId(delivery.vehicleId || "");

//     if (delivery.items && delivery.items.length > 0) {
//       setProducts(delivery.items.map((item, index) => ({
//         id: index.toString(),
//         productId: item.productId,
//         productName: item.productName || "",
//         quantity: item.quantity,
//         price: item.price || 0,
//       })));
//     } else {
//       setProducts([{ id: "1", productId: "", productName: "", quantity: 0, price: 0 }]);
//     }
//     window.scrollTo({ top: 0, behavior: 'smooth' });
//   };

//   const handleDelete = async (deliveryId: string) => {
//     if (!confirm("Are you sure?")) return;
//     setIsLoading(true);
//     try {
//       await shopSupplyService.delete(deliveryId);
//       toast({ title: "Success", description: "Deleted successfully" });
//       await loadDeliveries();
//     } catch (error) {
//         toast({ title: "Error", description: "Failed to delete", variant: "destructive" });
//     } finally {
//       setIsLoading(false);
//     }
//   };

//   const resetForm = () => {
//     setEditingDeliveryId(null);
//     setShopId("");
//     setDriverId("");
//     setVehicleId("");
//     setProducts([{ id: "1", productId: "", productName: "", quantity: 0, price: 0 }]);
//   };

//   return (
//     <div className="min-h-screen bg-gradient-to-br from-bakery-cream to-bakery-warm">
//       <header className="bg-white/80 backdrop-blur-sm border-b border-border shadow-[var(--shadow-soft)]">
//         <div className="container mx-auto px-4 py-4 flex items-center justify-between">
//           <div className="flex items-center space-x-3">
//             <Button variant="ghost" onClick={() => navigate("/dashboard")}> <ArrowLeft className="h-4 w-4" /> </Button>
//             <h1 className="text-xl font-bold text-bakery-brown">Deliver to Shops</h1>
//           </div>
//         </div>
//       </header>

//       <main className="container mx-auto px-4 py-8 space-y-8">
//         {/* Form Card */}
//         <div className="flex justify-center">
//           <Card className="w-full max-w-2xl">
//             <CardHeader>
//               <CardTitle>{editingDeliveryId ? "Update Delivery" : "New Shop Delivery / Assignment"}</CardTitle>
//             </CardHeader>
//             <CardContent className="space-y-4">
//               <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
//                 <div>
//                   <Label>Select Shop *</Label>
//                   <Select value={shopId} onValueChange={setShopId} disabled={isLoading}>
//                     <SelectTrigger><SelectValue placeholder="Select shop" /></SelectTrigger>
//                     <SelectContent>
//                       {availableShops.map((s) => (<SelectItem key={s.shopId} value={s.shopId!}>{s.shopName}</SelectItem>))}
//                     </SelectContent>
//                   </Select>
//                 </div>
//                 <div>
//                   <Label>Assign Driver</Label>
//                   <Select value={driverId} onValueChange={setDriverId} disabled={isLoading}>
//                     <SelectTrigger><SelectValue placeholder="Select driver" /></SelectTrigger>
//                     <SelectContent>
//                       {availableDrivers.map((d) => (<SelectItem key={d.driverId} value={d.driverId!}>{d.name}</SelectItem>))}
//                     </SelectContent>
//                   </Select>
//                 </div>
//               </div>
              
//               <div>
//                 <Label>Vehicle *</Label>
//                 <Select value={vehicleId} onValueChange={setVehicleId} disabled={isLoading}>
//                   <SelectTrigger><SelectValue placeholder="Select vehicle" /></SelectTrigger>
//                   <SelectContent>
//                     {availableVehicles.map((v) => (<SelectItem key={v.vehicleId} value={v.vehicleId!}>{v.vehicleNo}</SelectItem>))}
//                   </SelectContent>
//                 </Select>
//               </div>

//               <div className="space-y-3 mt-6">
//                 <div className="flex items-center justify-between">
//                   <Label className="text-base font-semibold">Products (Leave empty to Assign Job)</Label>
//                   <Button onClick={addProduct} size="sm" variant="outline" disabled={isLoading}><Plus className="h-4 w-4 mr-1" /> Add</Button>
//                 </div>
//                 <div className="space-y-2">
//                   {products.map((product) => (
//                     <div key={product.id} className="grid grid-cols-1 md:grid-cols-4 gap-2 p-3 border rounded-lg">
//                       <Select value={product.productId} onValueChange={(val) => updateProduct(product.id, "productId", val)} disabled={isLoading}>
//                         <SelectTrigger><SelectValue placeholder="Product" /></SelectTrigger>
//                         <SelectContent>{availableProducts.map((p) => (<SelectItem key={p.proId} value={p.proId!}>{p.name}</SelectItem>))}</SelectContent>
//                       </Select>
//                       <Input type="number" value={product.quantity} onChange={(e) => updateProduct(product.id, "quantity", Number(e.target.value))} placeholder="Qty" />
//                       <Input type="number" value={product.price} readOnly placeholder="Price" className="bg-muted"/>
//                       <div className="flex items-center gap-2">
//                          <Button variant="ghost" size="sm" onClick={() => removeProduct(product.id)}><Trash2 className="h-4 w-4 text-red-500" /></Button>
//                       </div>
//                     </div>
//                   ))}
//                 </div>
//               </div>

//               <div className="flex justify-end space-x-2 mt-6 border-t pt-4">
//                 <Button variant="outline" onClick={resetForm}>Clear</Button>
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ArrowLeft, Plus, Trash2, Edit2, Printer, Wallet } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { useToast } from "@/hooks/use-toast";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { shopSupplyService, ShopSupplyRequestDTO, ShopSupplyItemDTO, ShopSupplyResponseDTO } from "@/services/shop-supply.service";
import { productService, ProductDTO } from "@/services/product.service";
import { shopService, ShopDTO } from "@/services/shop.service";
import { driverService, DriverDTO } from "@/services/driver.service";
import { vehicleService, VehicleDTO } from "@/services/vehicle.service";
import { printBill } from "@/utils/printBill"; 

interface ProductItem {
  id: string;
  productId: string;
  productName: string;
  quantity: number;
  returnQuantity: number;
  expiredQuantity: number;
  price: number;
}

const ShopDelivery = () => {
  const navigate = useNavigate();
  const { toast } = useToast();
  
  const getCurrentUser = () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : { userId: "USR001", role: "OWNER" }; 
  };
  
  const currentUser = getCurrentUser();
  const isAdmin = currentUser?.role === 'OWNER';

  const [shopId, setShopId] = useState("");
  const [driverId, setDriverId] = useState(""); 
  const [products, setProducts] = useState<ProductItem[]>([
    { id: "1", productId: "", productName: "", quantity: 0, returnQuantity: 0, expiredQuantity: 0, price: 0 }
  ]);
  
  const [availableProducts, setAvailableProducts] = useState<ProductDTO[]>([]);
  const [availableShops, setAvailableShops] = useState<ShopDTO[]>([]);
  const [availableDrivers, setAvailableDrivers] = useState<any[]>([]);
  const [availableVehicles, setAvailableVehicles] = useState<VehicleDTO[]>([]);
  const [vehicleId, setVehicleId] = useState("");
  const [savedDeliveries, setSavedDeliveries] = useState<ShopSupplyResponseDTO[]>([]);
  
  const [editingDeliveryId, setEditingDeliveryId] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  
  const [filterShopId, setFilterShopId] = useState<string>("all");

  // Pagination and Accordion State
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;
  const [expandedRow, setExpandedRow] = useState<string | null>(null);

  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [paymentDeliveryId, setPaymentDeliveryId] = useState<string | null>(null);
  const [paymentAmount, setPaymentAmount] = useState<string>("");
  const [isSubmittingPayment, setIsSubmittingPayment] = useState(false);

  const handlePaymentSubmit = async () => {
    if (!paymentDeliveryId || !paymentAmount || isNaN(Number(paymentAmount)) || Number(paymentAmount) <= 0) {
      toast({ title: "Error", description: "Please enter a valid payment amount", variant: "destructive" });
      return;
    }
    
    setIsSubmittingPayment(true);
    try {
      await shopSupplyService.addPayment(paymentDeliveryId, Number(paymentAmount));
      toast({ title: "Success", description: "Payment recorded successfully" });
      setPaymentModalOpen(false);
      setPaymentAmount("");
      setPaymentDeliveryId(null);
      loadDeliveries();
    } catch (e: any) {
      toast({ title: "Error", description: e.response?.data?.message || "Payment failed", variant: "destructive" });
    } finally {
      setIsSubmittingPayment(false);
    }
  };

  useEffect(() => {
    setCurrentPage(1);
    setExpandedRow(null);
  }, [filterShopId]);

  useEffect(() => {
    loadAllData();
  }, []);

  const loadAllData = async () => {
    await Promise.all([
      loadProducts(),
      loadShops(),
      loadDrivers(),
      loadVehicles(),
      loadDeliveries()
    ]);
  };

  const loadProducts = async () => { try { setAvailableProducts(await productService.list()); } catch (e) {} };
  const loadShops = async () => { try { setAvailableShops(await shopService.list()); } catch (e) {} };
  const loadDrivers = async () => { try { setAvailableDrivers(await driverService.list()); } catch (e) {} };
  const loadVehicles = async () => { try { setAvailableVehicles(await vehicleService.list()); } catch (e) {} };
  const loadDeliveries = async () => { try { setSavedDeliveries(await shopSupplyService.list()); } catch (e) {} };

  const addProduct = () => {
    setProducts([...products, { id: Date.now().toString(), productId: "", productName: "", quantity: 0, returnQuantity: 0, expiredQuantity: 0, price: 0 }]);
  };

  const updateProduct = (id: string, field: keyof ProductItem, value: any) => {
    setProducts(products.map(product => {
      if (product.id === id) {
        if (field === 'productId' && typeof value === 'string') {
          const selectedProduct = availableProducts.find(p => p.proId === value);
          if (selectedProduct) {
            return { ...product, productId: value, productName: selectedProduct.name, price: selectedProduct.unitPrice };
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

  const handleSave = async () => {
    if (!shopId || !driverId || !vehicleId) {
      toast({ title: "Error", description: "Please select Shop, Driver and Vehicle", variant: "destructive" });
      return;
    }

    const validItems = products.filter(p => p.productId && p.quantity > 0);
    
    if (validItems.length === 0 && !confirm("Save as Assignment (No products)?")) {
        return;
    }

    setIsLoading(true);
    try {
      const items: ShopSupplyItemDTO[] = validItems.map(p => ({
        productId: p.productId,
        productName: p.productName,
        quantity: p.quantity,
        returnQuantity: p.returnQuantity,
        expiredQuantity: p.expiredQuantity,
        price: p.price,
        shopId: shopId,
      }));

      const requestBody: ShopSupplyRequestDTO = {
        shopId,
        salesmanId: currentUser.userId,
        driverId: driverId,
        vehicleId,
        items,
      };

      if (editingDeliveryId) {
        await shopSupplyService.update(editingDeliveryId, requestBody);
        toast({ title: "Success", description: "Delivery Completed & Saved" });
      } else {
        await shopSupplyService.create(requestBody);
        toast({ title: "Success", description: "Assignment Created" });
      }

      await loadDeliveries();
      resetForm();
    } catch (error: any) {
      console.error("Error saving:", error);
      toast({ title: "Error", description: "Failed to save.", variant: "destructive" });
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (delivery: ShopSupplyResponseDTO) => {
    setEditingDeliveryId(delivery.supplyId);
    setShopId(delivery.shopId || "");
    setDriverId(delivery.driverId || ""); 
    setVehicleId(delivery.vehicleId || "");

    if (delivery.items && delivery.items.length > 0) {
      setProducts(delivery.items.map((item, index) => ({
        id: index.toString(),
        productId: item.productId,
        productName: item.productName || "",
        quantity: item.quantity,
        returnQuantity: item.returnQuantity || 0,
        expiredQuantity: item.expiredQuantity || 0,
        price: item.price || 0,
      })));
    } else {
      setProducts([{ id: "1", productId: "", productName: "", quantity: 0, returnQuantity: 0, expiredQuantity: 0, price: 0 }]);
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleDelete = async (deliveryId: string) => {
    if (!confirm("Are you sure?")) return;
    setIsLoading(true);
    try {
      await shopSupplyService.delete(deliveryId);
      toast({ title: "Success", description: "Deleted successfully" });
      await loadDeliveries();
    } catch (error) {
        toast({ title: "Error", description: "Failed to delete", variant: "destructive" });
    } finally {
      setIsLoading(false);
    }
  };

  const resetForm = () => {
    setEditingDeliveryId(null);
    setShopId("");
    setDriverId("");
    setVehicleId("");
    setProducts([{ id: "1", productId: "", productName: "", quantity: 0, returnQuantity: 0, expiredQuantity: 0, price: 0 }]);
  };

  const visibleDeliveries = savedDeliveries.filter(delivery => {
    if (filterShopId !== "all" && delivery.shopId !== filterShopId) {
        return false;
    }
    if (isAdmin) return true;
    const isAssignment = delivery.items.length === 0;
    const isMyDelivery = delivery.salesmanId === currentUser.userId;
    return isAssignment || isMyDelivery;
  });

  return (
    <div className="min-h-screen bg-gradient-to-br from-bakery-cream to-bakery-warm">
      <header className="bg-white/80 backdrop-blur-sm border-b border-border shadow-[var(--shadow-soft)]">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <Button variant="ghost" onClick={() => navigate("/dashboard")}> <ArrowLeft className="h-4 w-4" /> </Button>
            <h1 className="text-xl font-bold text-bakery-brown">Deliver to Shops</h1>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-4 py-8 space-y-8">
        
        <div className="flex justify-center">
          <Card className="w-full max-w-2xl">
            <CardHeader>
              <CardTitle>
                {editingDeliveryId ? "Fulfill Assignment" : "Assign New Delivery"}
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <Label>Shop</Label>
                  <Select value={shopId} onValueChange={setShopId} disabled={isLoading}>
                    <SelectTrigger><SelectValue placeholder="Select shop" /></SelectTrigger>
                    <SelectContent>
                      {availableShops.map((s) => (<SelectItem key={s.shopId} value={s.shopId!}>{s.shopName}</SelectItem>))}
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <Label>Driver</Label>
                  <Select value={driverId} onValueChange={setDriverId} disabled={isLoading}>
                    <SelectTrigger><SelectValue placeholder="Select driver" /></SelectTrigger>
                    <SelectContent>
                      {availableDrivers.map((d) => (<SelectItem key={d.driverId} value={d.driverId!}>{d.name}</SelectItem>))}
                    </SelectContent>
                  </Select>
                </div>
              </div>
              
              <div>
                <Label>Vehicle</Label>
                <Select value={vehicleId} onValueChange={setVehicleId} disabled={isLoading}>
                  <SelectTrigger><SelectValue placeholder="Select vehicle" /></SelectTrigger>
                  <SelectContent>
                    {availableVehicles.map((v) => (<SelectItem key={v.vehicleId} value={v.vehicleId!}>{v.vehicleNo}</SelectItem>))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-3 mt-6">
                <div className="flex items-center justify-between mb-3">
                  <Label className="text-base font-semibold">Products</Label>
                  <Button onClick={addProduct} size="sm" variant="outline" disabled={isLoading}><Plus className="h-4 w-4 mr-1" /> Add</Button>
                </div>
                <div className="border rounded-lg overflow-hidden">
                  <table className="w-full text-sm text-left">
                    <thead className="text-xs text-muted-foreground uppercase bg-muted/50">
                      <tr>
                        <th className="px-4 py-3 font-medium">Product</th>
                        <th className="px-4 py-3 font-medium">Sent Qty</th>
                        <th className="px-4 py-3 font-medium">Return Qty</th>
                        <th className="px-4 py-3 font-medium">Expired Qty</th>
                        <th className="px-4 py-3 font-medium">Price (Rs.)</th>
                        <th className="px-4 py-3 w-[50px]"></th>
                      </tr>
                    </thead>
                    <tbody className="divide-y">
                      {products.map((product) => (
                        <tr key={product.id} className="bg-white">
                          <td className="p-3">
                            <Select value={product.productId} onValueChange={(val) => updateProduct(product.id, "productId", val)} disabled={isLoading}>
                              <SelectTrigger className="h-9"><SelectValue placeholder="Select" /></SelectTrigger>
                              <SelectContent>{availableProducts.map((p) => (<SelectItem key={p.proId} value={p.proId!}>{p.name}</SelectItem>))}</SelectContent>
                            </Select>
                          </td>
                          <td className="p-3">
                            <Input type="number" min="0" value={product.quantity} onChange={(e) => updateProduct(product.id, "quantity", Math.max(0, Number(e.target.value)))} placeholder="0" className="h-9" />
                          </td>
                          <td className="p-3">
                            <Input type="number" min="0" value={product.returnQuantity} onChange={(e) => updateProduct(product.id, "returnQuantity", Math.max(0, Number(e.target.value)))} placeholder="0" className="h-9" />
                          </td>
                          <td className="p-3">
                            <Input type="number" min="0" value={product.expiredQuantity} onChange={(e) => updateProduct(product.id, "expiredQuantity", Math.max(0, Number(e.target.value)))} placeholder="0" className="h-9" />
                          </td>
                          <td className="p-3">
                            <Input type="number" min="0" step="0.01" value={product.price} onChange={(e) => updateProduct(product.id, "price", Math.max(0, Number(e.target.value)))} placeholder="0.00" className="h-9"/>
                          </td>
                          <td className="p-3 text-center">
                            {products.length > 1 && (
                              <Button variant="ghost" size="sm" onClick={() => removeProduct(product.id)} disabled={isLoading}>
                                <Trash2 className="h-4 w-4 text-destructive" />
                              </Button>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>

              <div className="flex justify-end space-x-2 mt-6 border-t pt-4">
                <Button variant="outline" onClick={resetForm}>Cancel</Button>
                <Button onClick={handleSave} disabled={isLoading}>
                    {editingDeliveryId ? "Complete Delivery" : "Save Assignment"}
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-4">
          <div className="flex justify-between items-center bg-white/50 p-4 rounded-xl border border-white/60 shadow-sm backdrop-blur-sm">
            <h2 className="text-xl font-semibold text-bakery-brown">Saved Deliveries ({visibleDeliveries.length})</h2>
            <div className="flex items-center space-x-2">
              <Label className="text-sm font-medium text-bakery-brown whitespace-nowrap">Filter by Shop:</Label>
              <Select value={filterShopId} onValueChange={setFilterShopId}>
                <SelectTrigger className="w-[200px] bg-white">
                  <SelectValue placeholder="All Shops" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Shops</SelectItem>
                  {availableShops.map((s) => (
                    <SelectItem key={s.shopId} value={s.shopId!}>{s.shopName}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          
          {(() => {
            const totalPages = Math.ceil(visibleDeliveries.length / itemsPerPage);
            const paginatedDeliveries = visibleDeliveries.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

            return (
              <>
                {paginatedDeliveries.length > 0 ? (
                  paginatedDeliveries.map((delivery) => {
                    const isExpanded = expandedRow === delivery.supplyId;
                    return (
                        <Card key={delivery.supplyId} className="overflow-hidden border border-border shadow-sm">
                            <div 
                              className="p-4 bg-gray-50 flex justify-between items-center cursor-pointer hover:bg-black/5 transition-colors"
                              onClick={() => setExpandedRow(isExpanded ? null : delivery.supplyId)}
                            >
                                <div className="space-y-1">
                                    <div className="flex items-center gap-2">
                                        <h3 className="font-bold text-lg">{delivery.shopName}</h3>
                                        {delivery.paymentStatus === 'COMPLETED' ? (
                                            <span className="bg-green-200 text-green-800 text-xs px-2 py-1 rounded font-bold">COMPLETED</span>
                                        ) : delivery.items.length === 0 ? (
                                            <span className="bg-yellow-200 text-yellow-800 text-xs px-2 py-1 rounded font-bold">ASSIGNED - PENDING</span>
                                        ) : (
                                            <span className="bg-gray-200 text-gray-800 text-xs px-2 py-1 rounded font-bold">{delivery.paymentStatus || "UNCOMPLETED"}</span>
                                        )}
                                    </div>
                                    <p className="text-sm text-gray-600">{delivery.supplyDate} | Driver: {delivery.driverName} | Vehicle: {delivery.vehicleNo}</p>
                                    {delivery.items.length > 0 && (
                                        <p className="text-xs font-semibold text-gray-700">
                                          Paid: <span className="text-green-600">Rs. {Number(delivery.paidAmount || 0).toFixed(2)}</span> | 
                                          Outstanding: <span className="text-red-500">Rs. {Number(delivery.outstandingAmount || 0).toFixed(2)}</span>
                                        </p>
                                    )}
                                </div>
                                <div className="flex gap-2">
                                    {delivery.items.length > 0 && (
                                        <Button size="sm" variant="secondary" onClick={(e) => { e.stopPropagation(); printBill(delivery); }}><Printer className="h-4 w-4" /></Button>
                                    )}
                                    
                                    {delivery.items.length > 0 && delivery.paymentStatus !== 'COMPLETED' && (
                                        <Button size="sm" variant="outline" onClick={(e) => { 
                                          e.stopPropagation(); 
                                          setPaymentDeliveryId(delivery.supplyId); 
                                          setPaymentAmount(""); 
                                          setPaymentModalOpen(true); 
                                        }}>
                                          <Wallet className="h-4 w-4 text-green-600" />
                                        </Button>
                                    )}
                                    
                                    <Button size="sm" variant="outline" onClick={(e) => { e.stopPropagation(); handleEdit(delivery); }}>
                                        {delivery.items.length === 0 ? "Fulfill" : <Edit2 className="h-4 w-4" />}
                                    </Button>
                                    
                                    {isAdmin && (
                                        <Button size="sm" variant="destructive" onClick={(e) => { e.stopPropagation(); handleDelete(delivery.supplyId!); }}><Trash2 className="h-4 w-4" /></Button>
                                    )}
                                </div>
                            </div>

                            {isExpanded && delivery.items.length > 0 && (
                              <div className="p-4 border-t bg-white">
                                <div className="text-sm bg-gray-50/50 p-3 rounded border">
                                    {delivery.items.map((item, i) => (
                                        <div key={i} className="flex justify-between py-1 border-b last:border-0 border-gray-100">
                                            <span>{item.productName} (Sent: {item.quantity}, Ret: {item.returnQuantity || 0}, Exp: {item.expiredQuantity || 0})</span>
                                            <span className="font-medium text-gray-700">Rs. {((item.quantity - (item.returnQuantity || 0) - (item.expiredQuantity || 0)) * (item.price || 0)).toFixed(2)}</span>
                                        </div>
                                    ))}
                                    <div className="border-t mt-2 pt-2 font-bold text-right text-gray-900">
                                        Total: Rs. {Number(delivery.totalAmount || 0).toFixed(2)}
                                    </div>
                                </div>
                              </div>
                            )}
                        </Card>
                    );
                  })
                ) : (
                  <div className="text-center py-8 text-muted-foreground bg-white/50 rounded-xl border border-dashed">
                    No deliveries found matching your filter.
                  </div>
                )}

                {/* Pagination Controls */}
                {totalPages > 1 && (
                  <div className="flex items-center justify-center gap-4 mt-6">
                    <Button 
                      variant="outline" 
                      onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
                      disabled={currentPage === 1}
                    >
                      Previous
                    </Button>
                    <span className="text-sm text-muted-foreground font-medium">
                      Page {currentPage} of {totalPages}
                    </span>
                    <Button 
                      variant="outline" 
                      onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
                      disabled={currentPage === totalPages}
                    >
                      Next
                    </Button>
                  </div>
                )}
              </>
            );
          })()}
        </div>
      </main>
      
      {/* Payment Modal */}
      <Dialog open={paymentModalOpen} onOpenChange={setPaymentModalOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add Payment Installment</DialogTitle>
            <DialogDescription>
              Enter the payment amount for Delivery #{paymentDeliveryId}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label>Payment Amount (Rs.)</Label>
              <Input 
                type="number" 
                placeholder="0.00" 
                value={paymentAmount} 
                onChange={(e) => setPaymentAmount(e.target.value)}
                disabled={isSubmittingPayment}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setPaymentModalOpen(false)} disabled={isSubmittingPayment}>Cancel</Button>
            <Button onClick={handlePaymentSubmit} disabled={isSubmittingPayment}>Save Payment</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};
export default ShopDelivery;