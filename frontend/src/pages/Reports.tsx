import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ArrowLeft, FileText, Trash2, Calendar } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { fairDeliveryReportService, FairDeliveryReportDTO } from "@/services/fair-delivery-report.service";
import { shopSupplyReportService, ShopSupplyReportDTO } from "@/services/shop-supply-report.service";
import { fairDeliveryService, FairDeliveryDTO } from "@/services/fair-delivery.service";
import { shopSupplyService, ShopSupplyResponseDTO } from "@/services/shop-supply.service";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { format } from "date-fns";
import { useAuth } from "@/contexts/AuthContext";

const Reports = () => {
  const navigate = useNavigate();
  const { toast } = useToast();
  const { userRole, loading } = useAuth();

  // Fair Delivery Reports
  const [fairReports, setFairReports] = useState<any[]>([]);
  const [fairDailyDate, setFairDailyDate] = useState("");
  const [fairMonthlyDate, setFairMonthlyDate] = useState("");
  const [isLoadingFair, setIsLoadingFair] = useState(false);

  const [selectedFairReport, setSelectedFairReport] = useState<FairDeliveryReportDTO | null>(null);
  const [fairDeliveriesDetail, setFairDeliveriesDetail] = useState<FairDeliveryDTO[]>([]);
  const [isFairModalOpen, setIsFairModalOpen] = useState(false);
  const [isLoadingDetails, setIsLoadingDetails] = useState(false);
  const [expandedFairDelivery, setExpandedFairDelivery] = useState<string | null>(null);

  // Shop Supply Reports
  const [shopReports, setShopReports] = useState<any[]>([]);
  const [shopDailyDate, setShopDailyDate] = useState("");
  const [shopMonthlyDate, setShopMonthlyDate] = useState("");
  const [isLoadingShop, setIsLoadingShop] = useState(false);

  const [selectedShopReport, setSelectedShopReport] = useState<ShopSupplyReportDTO | null>(null);
  const [shopDeliveriesDetail, setShopDeliveriesDetail] = useState<ShopSupplyResponseDTO[]>([]);
  const [isShopModalOpen, setIsShopModalOpen] = useState(false);
  const [expandedShopDelivery, setExpandedShopDelivery] = useState<string | null>(null);

  useEffect(() => {
    if (!loading && !['ROLE_OWNER', 'ADMIN'].includes(userRole || '')) {
      navigate("/dashboard");
      return;
    }
    if (['ROLE_OWNER', 'ADMIN'].includes(userRole || '')) {
      loadFairReports();
      loadShopReports();
    }
  }, [loading, userRole, navigate]);

  const loadFairReports = async () => {
    try {
      const data = await fairDeliveryReportService.list();
      setFairReports(data.sort((a, b) => {
        const dateDiff = new Date(b.freportDate || b.reportMonth || '').getTime() - new Date(a.freportDate || a.reportMonth || '').getTime();
        return dateDiff !== 0 ? dateDiff : (b.reportId || '').localeCompare(a.reportId || '');
      }));
    } catch (error: any) {
      console.error("Failed to load fair reports:", error);
    }
  };

  const handleGenerateFairDaily = async () => {
    if (!fairDailyDate) {
      toast({ title: "Error", description: "Please select a date", variant: "destructive" });
      return;
    }
    setIsLoadingFair(true);
    try {
      await fairDeliveryReportService.generateDaily(fairDailyDate);
      toast({ title: "Success", description: "Report generated successfully" });
      await loadFairReports();
      setFairDailyDate("");
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Operation failed",
        variant: "destructive",
      });
    } finally {
      setIsLoadingFair(false);
    }
  };

  const handleGenerateFairMonthly = async () => {
    if (!fairMonthlyDate) {
      toast({ title: "Error", description: "Please select a month", variant: "destructive" });
      return;
    }
    setIsLoadingFair(true);
    try {
      const month = fairMonthlyDate.substring(0, 7);
      await fairDeliveryReportService.generateMonthly(month);
      toast({ title: "Success", description: "Report generated successfully" });
      await loadFairReports();
      setFairMonthlyDate("");
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Operation failed",
        variant: "destructive",
      });
    } finally {
      setIsLoadingFair(false);
    }
  };

  const handleDeleteFairReport = async (reportId: string) => {
    if (!confirm("Are you sure?")) return;
    setIsLoadingFair(true);
    try {
      await fairDeliveryReportService.delete(reportId);
      toast({ title: "Success", description: "Deleted successfully" });
      await loadFairReports();
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Operation failed",
        variant: "destructive",
      });
    } finally {
      setIsLoadingFair(false);
    }
  };

  const loadShopReports = async () => {
    try {
      const data = await shopSupplyReportService.list();
      setShopReports(data.sort((a, b) => {
        const dateDiff = new Date(b.sreportDate || b.reportMonth || '').getTime() - new Date(a.sreportDate || a.reportMonth || '').getTime();
        return dateDiff !== 0 ? dateDiff : (b.reportId || '').localeCompare(a.reportId || '');
      }));
    } catch (error: any) {
      console.error("Failed to load shop reports:", error);
    }
  };

  const handleGenerateShopDaily = async () => {
    if (!shopDailyDate) {
      toast({ title: "Error", description: "Please select a date", variant: "destructive" });
      return;
    }
    setIsLoadingShop(true);
    try {
      await shopSupplyReportService.generateDaily(shopDailyDate);
      toast({ title: "Success", description: "Report generated successfully" });
      await loadShopReports();
      setShopDailyDate("");
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Operation failed",
        variant: "destructive",
      });
    } finally {
      setIsLoadingShop(false);
    }
  };

  const handleGenerateShopMonthly = async () => {
    if (!shopMonthlyDate) {
      toast({ title: "Error", description: "Please select a month", variant: "destructive" });
      return;
    }
    setIsLoadingShop(true);
    try {
      const month = shopMonthlyDate.substring(0, 7);
      await shopSupplyReportService.generateMonthly(month);
      toast({ title: "Success", description: "Report generated successfully" });
      await loadShopReports();
      setShopMonthlyDate("");
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Operation failed",
        variant: "destructive",
      });
    } finally {
      setIsLoadingShop(false);
    }
  };

  const handleDeleteShopReport = async (reportId: string) => {
    if (!confirm("Are you sure?")) return;
    setIsLoadingShop(true);
    try {
      await shopSupplyReportService.delete(reportId);
      toast({ title: "Success", description: "Deleted successfully" });
      await loadShopReports();
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Operation failed",
        variant: "destructive",
      });
    } finally {
      setIsLoadingShop(false);
    }
  };

  const handleFairReportClick = async (report: FairDeliveryReportDTO) => {
    setSelectedFairReport(report);
    setExpandedFairDelivery(null);
    setIsFairModalOpen(true);
    setIsLoadingDetails(true);
    try {
      const dateOrMonth = report.reportType === 'DAILY' ? report.freportDate : report.reportMonth;
      const allDeliveries = await fairDeliveryService.list();
      let details: FairDeliveryDTO[] = [];

      if (report.reportType === 'DAILY' && dateOrMonth) {
        details = allDeliveries.filter(d => 
          d.deliveryDate === dateOrMonth && 
          ['RETURNED', 'RETURN', 'COMPLETED'].includes((d.status || '').toUpperCase())
        );
      } else if (report.reportType === 'MONTHLY' && dateOrMonth) {
        details = allDeliveries.filter(d => 
          d.deliveryDate?.startsWith(dateOrMonth) && 
          ['RETURNED', 'RETURN', 'COMPLETED'].includes((d.status || '').toUpperCase())
        );
      }

      setFairDeliveriesDetail(details);
    } catch (e) {
      toast({ title: "Error loading details", variant: "destructive" });
    } finally {
      setIsLoadingDetails(false);
    }
  };

  const handleShopReportClick = async (report: ShopSupplyReportDTO) => {
    setSelectedShopReport(report);
    setExpandedShopDelivery(null);
    setIsShopModalOpen(true);
    setIsLoadingDetails(true);
    try {
      const dateOrMonth = report.reportType === 'DAILY' ? report.sreportDate : report.reportMonth;
      const allDeliveries = await shopSupplyService.list();
      let details: ShopSupplyResponseDTO[] = [];

      if (report.reportType === 'DAILY' && dateOrMonth) {
        details = allDeliveries.filter(d => d.supplyDate === dateOrMonth);
      } else if (report.reportType === 'MONTHLY' && dateOrMonth) {
        details = allDeliveries.filter(d => d.supplyDate?.startsWith(dateOrMonth));
      }

      setShopDeliveriesDetail(details);
    } catch (e) {
      toast({ title: "Error loading details", variant: "destructive" });
    } finally {
      setIsLoadingDetails(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-bakery-cream to-bakery-warm">
      <header className="bg-white/80 backdrop-blur-sm border-b border-border shadow-[var(--shadow-soft)]">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <Button variant="ghost" onClick={() => navigate("/dashboard")}>
              <ArrowLeft className="h-4 w-4" />
            </Button>
            <h1 className="text-xl font-bold text-bakery-brown">Reports</h1>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-4 py-8">
        <Tabs defaultValue="fair-delivery" className="space-y-6">
          <TabsList className="grid w-full max-w-md mx-auto grid-cols-2">
            <TabsTrigger value="fair-delivery">Fair Delivery</TabsTrigger>
            <TabsTrigger value="shop-delivery">Shop Delivery</TabsTrigger>
          </TabsList>

          {/* FAIR DELIVERY REPORTS TAB */}
          <TabsContent value="fair-delivery" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Calendar className="h-5 w-5" /> Generate Daily Report
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <Input
                    type="date"
                    value={fairDailyDate}
                    onChange={(e) => setFairDailyDate(e.target.value)}
                    disabled={isLoadingFair}
                  />
                  <Button
                    onClick={handleGenerateFairDaily}
                    disabled={isLoadingFair}
                    className="w-full"
                  >
                    <FileText className="h-4 w-4 mr-2" />
                    {isLoadingFair ? "Generating..." : "Generate"}
                  </Button>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Calendar className="h-5 w-5" /> Generate Monthly Report
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <Input
                    type="month"
                    value={fairMonthlyDate}
                    onChange={(e) => setFairMonthlyDate(e.target.value)}
                    disabled={isLoadingFair}
                  />
                  <Button
                    onClick={handleGenerateFairMonthly}
                    disabled={isLoadingFair}
                    className="w-full"
                  >
                    <FileText className="h-4 w-4 mr-2" />
                    {isLoadingFair ? "Generating..." : "Generate"}
                  </Button>
                </CardContent>
              </Card>
            </div>

            <Card>
              <CardHeader>
                <CardTitle>Generated Reports ({fairReports.length})</CardTitle>
              </CardHeader>
              <CardContent>
                {fairReports.length === 0 ? (
                  <p className="text-center text-muted-foreground py-8">No reports</p>
                ) : (
                  <div className="overflow-auto">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>Report ID</TableHead>
                          <TableHead>Type</TableHead>
                          <TableHead>Date/Month</TableHead>
                          <TableHead>Deliveries</TableHead>
                          <TableHead>Revenue</TableHead>
                          <TableHead>Profit</TableHead>
                          <TableHead>Expenses</TableHead>
                          <TableHead>Generated</TableHead>
                          <TableHead>Actions</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {fairReports.map((report, index) => (
                          <TableRow
                            key={report.freportID || `fair-report-${index}`}
                            onClick={() => handleFairReportClick(report)}
                            className="cursor-pointer hover:bg-muted/50 transition-colors"
                          >
                            <TableCell className="font-mono text-xs">
                              {report.freportID}
                            </TableCell>
                            <TableCell>
                              <span className="px-2 py-1 rounded text-xs font-medium bg-blue-100 text-blue-700">
                                {report.reportType}
                              </span>
                            </TableCell>
                            <TableCell>
                              {report.reportType === 'DAILY' ? report.freportDate : report.reportMonth}
                            </TableCell>
                            <TableCell>{report.totalDeliveries}</TableCell>
                            <TableCell className="text-blue-600 font-medium">
                              Rs. {Number(report.totalRevenue || 0).toFixed(2)}
                            </TableCell>
                            <TableCell className="text-green-600 font-medium">
                              Rs. {Number(report.totalProfit || 0).toFixed(2)}
                            </TableCell>
                            <TableCell className="text-red-600 font-medium">
                              Rs. {Number(report.totalExpences || report.totalExpenses || 0).toFixed(2)}
                            </TableCell>
                            <TableCell className="text-xs text-muted-foreground">
                              {report.fgenaretedOn || report.fgeneretedOn
                                ? format(new Date(report.fgenaretedOn || report.fgeneretedOn), 'MMM dd, HH:mm')
                                : "N/A"}
                            </TableCell>
                            <TableCell>
                              <Button
                                size="sm"
                                variant="ghost"
                                onClick={(e) => { e.stopPropagation(); handleDeleteFairReport(report.reportId); }}
                                disabled={isLoadingFair}
                              >
                                <Trash2 className="h-4 w-4 text-destructive" />
                              </Button>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          {/* SHOP SUPPLY REPORTS TAB */}
          <TabsContent value="shop-delivery" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Calendar className="h-5 w-5" /> Generate Daily Report
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <Input
                    type="date"
                    value={shopDailyDate}
                    onChange={(e) => setShopDailyDate(e.target.value)}
                    disabled={isLoadingShop}
                  />
                  <Button
                    onClick={handleGenerateShopDaily}
                    disabled={isLoadingShop}
                    className="w-full"
                  >
                    <FileText className="h-4 w-4 mr-2" />
                    {isLoadingShop ? "Generating..." : "Generate"}
                  </Button>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Calendar className="h-5 w-5" /> Generate Monthly Report
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <Input
                    type="month"
                    value={shopMonthlyDate}
                    onChange={(e) => setShopMonthlyDate(e.target.value)}
                    disabled={isLoadingShop}
                  />
                  <Button
                    onClick={handleGenerateShopMonthly}
                    disabled={isLoadingShop}
                    className="w-full"
                  >
                    <FileText className="h-4 w-4 mr-2" />
                    {isLoadingShop ? "Generating..." : "Generate"}
                  </Button>
                </CardContent>
              </Card>
            </div>

            <Card>
              <CardHeader>
                <CardTitle>Generated Reports ({shopReports.length})</CardTitle>
              </CardHeader>
              <CardContent>
                {shopReports.length === 0 ? (
                  <p className="text-center text-muted-foreground py-8">No reports</p>
                ) : (
                  <div className="overflow-auto">
                    <Table>
                      <TableHeader>
                        <TableRow>
                          <TableHead>Report ID</TableHead>
                          <TableHead>Type</TableHead>
                          <TableHead>Date/Month</TableHead>
                          <TableHead>Total Supplies</TableHead>
                          <TableHead>Total Sales</TableHead>
                          <TableHead>Shops Served</TableHead>
                          <TableHead>Generated</TableHead>
                          <TableHead>Actions</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {shopReports.map((report, index) => (
                          <TableRow
                            key={report.sreportId || `shop-report-${index}`}
                            onClick={() => handleShopReportClick(report)}
                            className="cursor-pointer hover:bg-muted/50 transition-colors"
                          >
                            <TableCell className="font-mono text-xs">
                              {report.sreportId}
                            </TableCell>
                            <TableCell>
                              <span className="px-2 py-1 rounded text-xs font-medium bg-purple-100 text-purple-700">
                                {report.reportType}
                              </span>
                            </TableCell>
                            <TableCell>
                              {report.reportType === 'DAILY' ? report.sreportDate : report.reportMonth}
                            </TableCell>
                            <TableCell>{report.totalSupplies}</TableCell>
                            <TableCell className="text-blue-600 font-medium">
                              Rs. {Number(report.totalAmount || 0).toFixed(2)}
                            </TableCell>
                            <TableCell>{report.totalShopsServed}</TableCell>
                            <TableCell className="text-xs text-muted-foreground">
                              {report.sgeneratedOn || report.sgeneretedOn
                                ? format(new Date(report.sgeneratedOn || report.sgeneretedOn), 'MMM dd, HH:mm')
                                : "N/A"}
                            </TableCell>
                            <TableCell>
                              <Button
                                size="sm"
                                variant="ghost"
                                onClick={(e) => { e.stopPropagation(); handleDeleteShopReport(report.reportId || report.sreportId); }}
                                disabled={isLoadingShop}
                              >
                                <Trash2 className="h-4 w-4 text-destructive" />
                              </Button>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>

        {/* Fair Delivery Detail Modal */}
        <Dialog open={isFairModalOpen} onOpenChange={setIsFairModalOpen}>
          <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>
                Report Details: {selectedFairReport?.reportId}
              </DialogTitle>
              <DialogDescription>
                {selectedFairReport?.reportType === 'DAILY' ? 'Daily' : 'Monthly'} Report for {selectedFairReport?.reportType === 'DAILY' ? selectedFairReport.freportDate : selectedFairReport?.reportMonth}
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div className="grid grid-cols-3 gap-4 border-b pb-4">
                <div>
                  <p className="text-sm text-muted-foreground">Total Revenue</p>
                  <p className="text-lg font-semibold text-blue-600">Rs. {Number(selectedFairReport?.totalRevenue || 0).toFixed(2)}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Total Profit</p>
                  <p className="text-lg font-semibold text-green-600">Rs. {Number(selectedFairReport?.totalProfit || 0).toFixed(2)}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Total Expenses</p>
                  <p className="text-lg font-semibold text-red-600">Rs. {Number(selectedFairReport?.totalExpences || 0).toFixed(2)}</p>
                </div>
              </div>

              <h3 className="font-medium">Included Deliveries ({fairDeliveriesDetail.length})</h3>
              {isLoadingDetails ? (
                <div className="text-center py-4 text-muted-foreground">Loading deliveries...</div>
              ) : fairDeliveriesDetail.length === 0 ? (
                <div className="text-center py-4 text-muted-foreground">No deliveries found for this report.</div>
              ) : (
                <div className="border rounded-md">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Delivery ID</TableHead>
                        <TableHead>Fair Name</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead>Revenue</TableHead>
                        <TableHead>Expenses</TableHead>
                        <TableHead>Profit</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {fairDeliveriesDetail.map((d, i) => {
                        const revenue = d.items?.reduce((sum, item) => sum + ((item.qtySent - (item.qtyRemaining || 0) - (item.qtyExpired || 0)) * (item.unitPrice || 0)), 0) || 0;
                        const expenses = (d.tax || 0) + (d.extraPayments || 0) + (d.dieselAmount || 0);
                        const isExpanded = expandedFairDelivery === (d.deliveryId || String(i));
                        return (
                          <React.Fragment key={d.deliveryId || i}>
                            <TableRow
                              className="cursor-pointer hover:bg-gray-50"
                              onClick={() => setExpandedFairDelivery(isExpanded ? null : (d.deliveryId || String(i)))}
                            >
                              <TableCell className="font-mono text-xs">{d.deliveryId}</TableCell>
                              <TableCell>{d.fairName}</TableCell>
                              <TableCell>
                                <span className={`px-2 py-1 rounded text-[10px] font-bold ${d.status === 'OUT' ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'}`}>
                                  {d.status}
                                </span>
                              </TableCell>
                              <TableCell className="text-blue-600">Rs. {revenue.toFixed(2)}</TableCell>
                              <TableCell className="text-red-600">Rs. {expenses.toFixed(2)}</TableCell>
                              <TableCell className="text-green-600 font-medium">Rs. {Number(d.profit || 0).toFixed(2)}</TableCell>
                            </TableRow>
                            {isExpanded && d.items && d.items.length > 0 && (
                              <TableRow>
                                <TableCell colSpan={6} className="bg-gray-50 p-4">
                                  <div className="text-sm bg-white p-3 rounded border shadow-sm">
                                    <h4 className="font-semibold mb-2 text-gray-700">Product Breakdown</h4>
                                    {d.items.map((item, idx) => (
                                      <div key={idx} className="flex justify-between py-1 border-b last:border-0 border-gray-100">
                                        {/* Fallback to productId if name not populated in DTO */}
                                        <span>{item.productId} (Sent: {item.qtySent}, Ret: {item.qtyRemaining || 0}, Exp: {item.qtyExpired || 0})</span>
                                        <span className="font-medium">Rs. {Number((item.qtySent - (item.qtyRemaining || 0) - (item.qtyExpired || 0)) * (item.unitPrice || 0)).toFixed(2)}</span>
                                      </div>
                                    ))}
                                    <div className="border-t mt-2 pt-2 font-bold text-right text-gray-900">
                                      Total Sales: Rs. {revenue.toFixed(2)}
                                    </div>
                                  </div>
                                </TableCell>
                              </TableRow>
                            )}
                          </React.Fragment>
                        );
                      })}
                    </TableBody>
                  </Table>
                </div>
              )}
            </div>
          </DialogContent>
        </Dialog>

        {/* Shop Supply Detail Modal */}
        <Dialog open={isShopModalOpen} onOpenChange={setIsShopModalOpen}>
          <DialogContent className="max-w-4xl max-h-[80vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>
                Report Details: {selectedShopReport?.reportId}
              </DialogTitle>
              <DialogDescription>
                {selectedShopReport?.reportType === 'DAILY' ? 'Daily' : 'Monthly'} Report for {selectedShopReport?.reportType === 'DAILY' ? selectedShopReport.sreportDate : selectedShopReport?.reportMonth}
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              {(() => {
                const totalSales = shopDeliveriesDetail.reduce((sum, d) => sum + Number(d.totalAmount || 0), 0);
                const totalIncome = shopDeliveriesDetail.reduce((sum, d) => sum + Number(d.paidAmount || 0), 0);
                const totalOutstanding = shopDeliveriesDetail.reduce((sum, d) => sum + Number(d.outstandingAmount || 0), 0);

                return (
                  <div className="grid grid-cols-4 gap-4 border-b pb-4">
                    <div>
                      <p className="text-sm text-muted-foreground">Total Supplies</p>
                      <p className="text-lg font-semibold">{selectedShopReport?.totalSupplies || 0}</p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Total Sales</p>
                      <p className="text-lg font-semibold text-blue-600">Rs. {totalSales.toFixed(2)}</p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Income (Paid)</p>
                      <p className="text-lg font-semibold text-green-600">Rs. {totalIncome.toFixed(2)}</p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Outstanding</p>
                      <p className="text-lg font-semibold text-red-600">Rs. {totalOutstanding.toFixed(2)}</p>
                    </div>
                  </div>
                );
              })()}

              <h3 className="font-medium">Included Supplies ({shopDeliveriesDetail.length})</h3>
              {isLoadingDetails ? (
                <div className="text-center py-4 text-muted-foreground">Loading supplies...</div>
              ) : shopDeliveriesDetail.length === 0 ? (
                <div className="text-center py-4 text-muted-foreground">No supplies found for this report.</div>
              ) : (
                <div className="border rounded-md">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Supply ID</TableHead>
                        <TableHead>Shop Name</TableHead>
                        <TableHead>Driver</TableHead>
                        <TableHead>Vehicle</TableHead>
                        <TableHead>Status</TableHead>
                        <TableHead>Total Sales</TableHead>
                        <TableHead>Paid</TableHead>
                        <TableHead>Outstanding</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {shopDeliveriesDetail.map((d, i) => {
                        const isExpanded = expandedShopDelivery === (d.supplyId || String(i));
                        return (
                          <React.Fragment key={d.supplyId || i}>
                            <TableRow
                              className="cursor-pointer hover:bg-gray-50"
                              onClick={() => setExpandedShopDelivery(isExpanded ? null : (d.supplyId || String(i)))}
                            >
                              <TableCell className="font-mono text-xs">{d.supplyId}</TableCell>
                              <TableCell>{d.shopName}</TableCell>
                              <TableCell>{d.driverName}</TableCell>
                              <TableCell>{d.vehicleNo}</TableCell>
                              <TableCell>
                                {d.items && d.items.length === 0 ? (
                                  <span className="bg-yellow-200 text-yellow-800 text-xs px-2 py-1 rounded font-bold">ASSIGNED</span>
                                ) : (
                                  <span className={`text-xs px-2 py-1 rounded font-bold ${d.paymentStatus === 'COMPLETED' ? 'bg-green-200 text-green-800' : 'bg-gray-200 text-gray-800'}`}>{d.paymentStatus || 'COMPLETED'}</span>
                                )}
                              </TableCell>
                              <TableCell className="text-blue-600 font-medium">Rs. {Number(d.totalAmount || 0).toFixed(2)}</TableCell>
                              <TableCell className="text-green-600 font-medium">Rs. {Number(d.paidAmount || 0).toFixed(2)}</TableCell>
                              <TableCell className="text-red-600 font-medium">Rs. {Number(d.outstandingAmount || 0).toFixed(2)}</TableCell>
                            </TableRow>
                            {isExpanded && d.items && d.items.length > 0 && (
                              <TableRow>
                                <TableCell colSpan={8} className="bg-gray-50 p-4">
                                  <div className="text-sm bg-white p-3 rounded border shadow-sm">
                                    <h4 className="font-semibold mb-2 text-gray-700">Product Breakdown</h4>
                                    {d.items.map((item, idx) => (
                                      <div key={idx} className="flex justify-between py-1 border-b last:border-0 border-gray-100">
                                        <span>{item.productName} (Sent: {item.quantity}, Ret: {item.returnQuantity || 0}, Exp: {item.expiredQuantity || 0})</span>
                                        <span className="font-medium">Rs. {((item.quantity - (item.returnQuantity || 0) - (item.expiredQuantity || 0)) * (item.price || 0)).toFixed(2)}</span>
                                      </div>
                                    ))}
                                    <div className="border-t mt-2 pt-2 font-bold text-right text-gray-900">
                                      Total Sales: Rs. {Number(d.totalAmount || 0).toFixed(2)}
                                    </div>
                                  </div>
                                </TableCell>
                              </TableRow>
                            )}
                          </React.Fragment>
                        );
                      })}
                    </TableBody>
                  </Table>
                </div>
              )}
            </div>
          </DialogContent>
        </Dialog>

      </main>
    </div>
  );
};

export default Reports;