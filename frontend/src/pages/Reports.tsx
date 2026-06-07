import { useState, useEffect } from "react";
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

  // Shop Supply Reports
  const [shopReports, setShopReports] = useState<any[]>([]);
  const [shopDailyDate, setShopDailyDate] = useState("");
  const [shopMonthlyDate, setShopMonthlyDate] = useState("");
  const [isLoadingShop, setIsLoadingShop] = useState(false);

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
      setFairReports(data);
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
      setShopReports(data);
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
                          <TableRow key={report.freportID || `fair-report-${index}`}>
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
                            <TableCell className="text-green-600 font-medium">
                              Rs. {Number(report.totalRevenue || 0).toFixed(2)}
                            </TableCell>
                            <TableCell className="text-blue-600 font-medium">
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
                                onClick={() => handleDeleteFairReport(report.freportID)} 
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
                          <TableHead>Total Amount</TableHead>
                          <TableHead>Shops Served</TableHead>
                          <TableHead>Generated</TableHead>
                          <TableHead>Actions</TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {shopReports.map((report, index) => (
                          <TableRow key={report.sreportId || `shop-report-${index}`}>
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
                            <TableCell className="text-green-600 font-medium">
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
                                onClick={() => handleDeleteShopReport(report.sreportId)} 
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
      </main>
    </div>
  );
};

export default Reports;