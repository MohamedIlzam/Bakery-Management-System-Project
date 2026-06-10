import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { ArrowLeft, Loader2, User, Mail, ShieldAlert, KeyRound, CheckCircle } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { authService } from "@/services/auth.service";
import { useAuth } from "@/contexts/AuthContext";

const Profile = () => {
  const navigate = useNavigate();
  const { toast } = useToast();
  const { logout, loading: authLoading, isAuthenticated } = useAuth();

  const [profileData, setProfileData] = useState<{ username: string; role: string; recoveryEmail: string | null } | null>(null);
  const [isLoadingProfile, setIsLoadingProfile] = useState(true);

  // Edit details state
  const [newUsername, setNewUsername] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isUpdatingDetails, setIsUpdatingDetails] = useState(false);

  // Email update state
  const [newEmail, setNewEmail] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const [codeSent, setCodeSent] = useState(false);
  const [isSendingCode, setIsSendingCode] = useState(false);
  const [isVerifyingCode, setIsVerifyingCode] = useState(false);

  // Redirect if not authenticated
  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      navigate("/login");
    }
  }, [isAuthenticated, authLoading, navigate]);

  // Load user profile details
  const loadProfile = async () => {
    setIsLoadingProfile(true);
    try {
      const data = await authService.getProfile();
      setProfileData(data);
      setNewUsername(data.username);
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to load profile details",
        variant: "destructive",
      });
    } finally {
      setIsLoadingProfile(false);
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      loadProfile();
    }
  }, [isAuthenticated]);

  const handleUpdateDetails = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newUsername.trim()) {
      toast({
        title: "Error",
        description: "Username is required",
        variant: "destructive",
      });
      return;
    }

    if (newPassword && newPassword.length < 6) {
      toast({
        title: "Error",
        description: "Password must be at least 6 characters long",
        variant: "destructive",
      });
      return;
    }

    if (newPassword !== confirmPassword) {
      toast({
        title: "Error",
        description: "Passwords do not match",
        variant: "destructive",
      });
      return;
    }

    setIsUpdatingDetails(true);
    try {
      const payload: any = { username: newUsername };
      if (newPassword) payload.password = newPassword;

      await authService.updateProfile(payload);
      toast({
        title: "Success",
        description: "Account details updated successfully",
      });

      // If username changed, they must log in again due to session changes
      if (profileData && newUsername !== profileData.username) {
        toast({
          title: "Session Expired",
          description: "Your username changed. Please log in again.",
        });
        logout();
        navigate("/login");
      } else {
        setNewPassword("");
        setConfirmPassword("");
        loadProfile();
      }
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to update profile",
        variant: "destructive",
      });
    } finally {
      setIsUpdatingDetails(false);
    }
  };

  const handleSendEmailCode = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newEmail.trim()) {
      toast({
        title: "Error",
        description: "Please enter a valid email address",
        variant: "destructive",
      });
      return;
    }

    setIsSendingCode(true);
    try {
      await authService.sendEmailUpdateCode(newEmail);
      toast({
        title: "Verification Code Sent",
        description: "Verification code sent to the proposed new email",
      });
      setCodeSent(true);
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to send verification code",
        variant: "destructive",
      });
    } finally {
      setIsSendingCode(false);
    }
  };

  const handleVerifyEmailCode = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!verificationCode.trim()) {
      toast({
        title: "Error",
        description: "Please enter the verification code",
        variant: "destructive",
      });
      return;
    }

    setIsVerifyingCode(true);
    try {
      await authService.verifyEmailUpdateCode(newEmail, verificationCode);
      toast({
        title: "Success",
        description: "Recovery email has been updated successfully",
      });
      setCodeSent(false);
      setNewEmail("");
      setVerificationCode("");
      loadProfile();
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Invalid or expired code",
        variant: "destructive",
      });
    } finally {
      setIsVerifyingCode(false);
    }
  };

  const formatRole = (role: string) => {
    if (!role) return "";
    let cleanRole = role.replace(/^ROLE_/, "");
    if (cleanRole === "SALESMAN") return "Salesman";
    if (cleanRole === "OWNER") return "Owner";
    if (cleanRole === "DRIVER") return "Driver";
    return cleanRole.charAt(0).toUpperCase() + cleanRole.slice(1).toLowerCase();
  };

  if (isLoadingProfile || authLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-bakery-cream to-bakery-warm">
        <div className="text-center">
          <Loader2 className="h-12 w-12 animate-spin text-primary mx-auto mb-4" />
          <p className="text-muted-foreground">Loading profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-bakery-cream to-bakery-warm p-4">
      <div className="max-w-4xl mx-auto">
        {/* Back Button */}
        <div className="mb-6 flex items-center">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate("/dashboard")}
            className="mr-4 text-bakery-brown"
          >
            <ArrowLeft className="h-5 w-5" />
          </Button>
          <div>
            <h1 className="text-3xl font-bold text-bakery-brown">My Profile</h1>
            <p className="text-muted-foreground">Manage your credentials and recovery options</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Summary / Role card */}
          <Card className="col-span-1 shadow-[var(--shadow-card)]">
            <CardHeader className="text-center">
              <div className="w-20 h-20 bg-gradient-to-br from-primary to-accent rounded-full flex items-center justify-center mx-auto shadow-[var(--shadow-soft)] mb-4">
                <User className="h-10 w-10 text-primary-foreground" />
              </div>
              <CardTitle className="text-xl font-bold text-bakery-brown">{profileData?.username}</CardTitle>
              <CardDescription className="capitalize font-semibold text-accent mt-1">
                {formatRole(profileData?.role || "")}
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4 pt-4 border-t border-border">
              <div className="flex flex-col space-y-1">
                <span className="text-xs text-muted-foreground font-semibold uppercase tracking-wider">Recovery Email</span>
                <span className="text-sm font-medium text-bakery-brown break-all flex items-center gap-1.5">
                  <Mail className="h-4 w-4 shrink-0 text-muted-foreground" />
                  {profileData?.recoveryEmail || (
                    <span className="text-destructive font-normal italic flex items-center gap-1">
                      <ShieldAlert className="h-4 w-4" /> Not Set
                    </span>
                  )}
                </span>
              </div>
            </CardContent>
          </Card>

          {/* Edit form cards */}
          <div className="col-span-1 md:col-span-2 space-y-6">
            {/* Account details modification */}
            <Card className="shadow-[var(--shadow-card)]">
              <CardHeader>
                <CardTitle className="text-lg text-bakery-brown flex items-center gap-2">
                  <User className="h-5 w-5 text-primary" /> Update Account Details
                </CardTitle>
                <CardDescription>Update your username or reset your account password.</CardDescription>
              </CardHeader>
              <CardContent>
                <form onSubmit={handleUpdateDetails} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="newUsername">Username</Label>
                    <Input
                      id="newUsername"
                      value={newUsername}
                      onChange={(e) => setNewUsername(e.target.value)}
                      required
                      disabled={isUpdatingDetails}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="newPassword">New Password (Optional)</Label>
                    <Input
                      id="newPassword"
                      type="password"
                      placeholder="Leave blank to keep current"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      disabled={isUpdatingDetails}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="confirmPassword">Confirm New Password</Label>
                    <Input
                      id="confirmPassword"
                      type="password"
                      placeholder="Leave blank to keep current"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      disabled={isUpdatingDetails}
                    />
                  </div>
                  <Button type="submit" disabled={isUpdatingDetails} className="w-full sm:w-auto">
                    {isUpdatingDetails && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                    Save Details
                  </Button>
                </form>
              </CardContent>
            </Card>

            {/* Recovery email change */}
            <Card className="shadow-[var(--shadow-card)]">
              <CardHeader>
                <CardTitle className="text-lg text-bakery-brown flex items-center gap-2">
                  <Mail className="h-5 w-5 text-accent" /> Configure Recovery Email
                </CardTitle>
                <CardDescription>
                  Adding a verified recovery email lets you reset your password if you forget it.
                </CardDescription>
              </CardHeader>
              <CardContent>
                {!codeSent ? (
                  <form onSubmit={handleSendEmailCode} className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="newEmail">New Recovery Email</Label>
                      <Input
                        id="newEmail"
                        type="email"
                        placeholder="newemail@example.com"
                        value={newEmail}
                        onChange={(e) => setNewEmail(e.target.value)}
                        required
                        disabled={isSendingCode}
                      />
                    </div>
                    <Button type="submit" variant="outline" disabled={isSendingCode} className="w-full sm:w-auto flex items-center gap-2">
                      {isSendingCode ? <Loader2 className="h-4 w-4 animate-spin" /> : <KeyRound className="h-4 w-4" />}
                      Send Verification Code
                    </Button>
                  </form>
                ) : (
                  <form onSubmit={handleVerifyEmailCode} className="space-y-4">
                    <div className="bg-blue-50 border border-blue-200 text-blue-800 p-3 rounded-lg text-sm flex items-start gap-2.5">
                      <CheckCircle className="h-5 w-5 text-blue-600 shrink-0 mt-0.5" />
                      <div>
                        Verification code sent to <strong>{newEmail}</strong>. Please enter the code below to confirm and link this email.
                      </div>
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="verificationCode">6-Digit Code</Label>
                      <Input
                        id="verificationCode"
                        type="text"
                        maxLength={6}
                        placeholder="000000"
                        className="text-center text-lg tracking-widest font-semibold"
                        value={verificationCode}
                        onChange={(e) => setVerificationCode(e.target.value)}
                        required
                        disabled={isVerifyingCode}
                      />
                    </div>
                    <div className="flex flex-col sm:flex-row gap-2">
                      <Button type="submit" disabled={isVerifyingCode} className="flex-1">
                        {isVerifyingCode && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                        Confirm Code & Save
                      </Button>
                      <Button
                        type="button"
                        variant="ghost"
                        onClick={() => {
                          setCodeSent(false);
                          setVerificationCode("");
                        }}
                        disabled={isVerifyingCode}
                      >
                        Cancel
                      </Button>
                    </div>
                  </form>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
