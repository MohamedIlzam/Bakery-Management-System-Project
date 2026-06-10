import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Truck, Store, FileText, LogOut, Croissant, Plus, User } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";

const Dashboard = () => {
  const navigate = useNavigate();
  const { isAuthenticated, userRole, username, logout, loading } = useAuth();

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!loading && !isAuthenticated) {
      navigate("/login");
    }
  }, [isAuthenticated, loading, navigate]);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  // Show loading state while checking authentication
  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-bakery-cream to-bakery-warm">
        <div className="text-center">
          <Croissant className="h-16 w-16 animate-pulse text-primary mx-auto mb-4" />
          <p className="text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  // Don't render the dashboard if not authenticated
  if (!isAuthenticated) {
    return null;
  }

  const dashboardCards = [
    {
      title: "Deliver to Fair",
      description: "Manage daily deliveries to fairs with vehicle tracking and profit calculation",
      icon: Truck,
      path: "/fair-delivery",
      color: "from-primary to-accent",
    },
    {
      title: "Deliver to Shops",
      description: "Track deliveries to various shops with detailed product lists",
      icon: Store,
      path: "/shop-delivery",
      color: "from-accent to-bakery-gold",
    },
    {
      title: "Reports",
      description: "View daily and monthly reports with profit analysis",
      icon: FileText,
      path: "/reports",
      color: "from-bakery-gold to-primary",
      roles: ["ROLE_OWNER", "ADMIN"],
    },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-bakery-cream to-bakery-warm">
      {/* Header */}
      <header className="bg-white/80 backdrop-blur-sm border-b border-border shadow-[var(--shadow-soft)]">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-gradient-to-br from-primary to-accent rounded-full flex items-center justify-center">
              <Croissant className="h-5 w-5 text-primary-foreground" />
            </div>
            <div>
              <h1 className="text-xl font-bold text-bakery-brown">Kodikara Bake House</h1>
              <p className="text-sm text-muted-foreground">Welcome, {username} </p>
            </div>
          </div>
          <div className="flex items-center space-x-2">
            <Button 
              variant="outline" 
              onClick={() => navigate("/profile")}
              className="flex items-center space-x-2"
            >
              <User className="h-4 w-4" />
              <span>Profile</span>
            </Button>
            <Button 
              variant="outline" 
              onClick={handleLogout}
              className="flex items-center space-x-2"
            >
              <LogOut className="h-4 w-4" />
              <span>Logout</span>
            </Button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-3xl font-bold text-bakery-brown mb-2">Management Dashboard</h2>
              <p className="text-muted-foreground">Select an operation to manage your bakery business</p>
            </div>
            {['ROLE_OWNER', 'ADMIN'].includes(userRole || '') && (
              <Button
                onClick={() => navigate("/manage")}
                className="flex items-center gap-2 bg-gradient-to-r from-primary to-accent hover:opacity-90"
              >
                <Plus className="h-4 w-4" />
                Manage Data
              </Button>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {dashboardCards.filter(card => !card.roles || card.roles.includes(userRole || '')).map((card) => {
            const Icon = card.icon;
            return (
              <Card 
                key={card.path}
                className="hover:shadow-[var(--shadow-card)] transition-all duration-300 hover:-translate-y-1 cursor-pointer group"
                onClick={() => navigate(card.path)}
              >
                <CardHeader>
                  <div className={`w-12 h-12 bg-gradient-to-br ${card.color} rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300`}>
                    <Icon className="h-6 w-6 text-white" />
                  </div>
                  <CardTitle className="text-xl text-bakery-brown">{card.title}</CardTitle>
                  <CardDescription className="text-muted-foreground">
                    {card.description}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <Button 
                    className={`w-full bg-gradient-to-r ${card.color} hover:opacity-90 shadow-[var(--shadow-soft)]`}
                  >
                    Open {card.title}
                  </Button>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </main>
    </div>
  );
};

export default Dashboard;