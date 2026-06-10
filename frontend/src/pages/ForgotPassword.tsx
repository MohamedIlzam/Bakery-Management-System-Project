import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { KeyRound, Mail, Lock, ArrowLeft, Loader2 } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { authService } from "@/services/auth.service";

type RecoveryStep = "request" | "verify" | "reset";

const ForgotPassword = () => {
  const [step, setStep] = useState<RecoveryStep>("request");
  const [usernameOrEmail, setUsernameOrEmail] = useState("");
  const [code, setCode] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { toast } = useToast();

  const handleRequestCode = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!usernameOrEmail.trim()) {
      toast({
        title: "Error",
        description: "Please enter your username or recovery email",
        variant: "destructive",
      });
      return;
    }

    setIsLoading(true);
    try {
      const response = await authService.forgotPassword(usernameOrEmail);
      toast({
        title: "Code Sent",
        description: response.message || "Verification code sent to recovery email",
      });
      setStep("verify");
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "User not found or email send failed",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerifyCode = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!code.trim()) {
      toast({
        title: "Error",
        description: "Please enter the verification code",
        variant: "destructive",
      });
      return;
    }

    setIsLoading(true);
    try {
      await authService.verifyCode(usernameOrEmail, code);
      toast({
        title: "Success",
        description: "Code verified. Please enter your new password.",
      });
      setStep("reset");
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Invalid or expired code",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleResetPassword = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPassword || !confirmPassword) {
      toast({
        title: "Error",
        description: "Please fill in all fields",
        variant: "destructive",
      });
      return;
    }

    if (newPassword.length < 6) {
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

    setIsLoading(true);
    try {
      await authService.resetPassword(usernameOrEmail, code, newPassword);
      toast({
        title: "Success",
        description: "Password reset successfully. You can now login.",
      });
      navigate("/login");
    } catch (error: any) {
      toast({
        title: "Error",
        description: error.response?.data?.message || "Failed to reset password",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-bakery-cream to-bakery-warm p-4">
      <Card className="w-full max-w-md shadow-[var(--shadow-card)]">
        <CardHeader className="text-center relative">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => {
              if (step === "verify") setStep("request");
              else if (step === "reset") setStep("verify");
              else navigate("/login");
            }}
            className="absolute left-4 top-4 text-bakery-brown"
          >
            <ArrowLeft className="h-5 w-5" />
          </Button>

          <div className="flex justify-center mb-4 mt-2">
            <div className="w-16 h-16 bg-gradient-to-br from-primary to-accent rounded-full flex items-center justify-center shadow-[var(--shadow-soft)]">
              {step === "request" && <Mail className="h-8 w-8 text-primary-foreground" />}
              {step === "verify" && <KeyRound className="h-8 w-8 text-primary-foreground" />}
              {step === "reset" && <Lock className="h-8 w-8 text-primary-foreground" />}
            </div>
          </div>
          <CardTitle className="text-2xl font-bold text-bakery-brown">
            {step === "request" && "Forgot Password"}
            {step === "verify" && "Enter Code"}
            {step === "reset" && "Reset Password"}
          </CardTitle>
          <CardDescription>
            {step === "request" && "Enter your username or recovery email to get a 6-digit verification code."}
            {step === "verify" && `We've sent a code. Check your recovery email for the verification code.`}
            {step === "reset" && "Choose a strong new password for your account."}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {step === "request" && (
            <form onSubmit={handleRequestCode} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="usernameOrEmail">Username or Recovery Email</Label>
                <Input
                  id="usernameOrEmail"
                  type="text"
                  placeholder="Enter username or email"
                  value={usernameOrEmail}
                  onChange={(e) => setUsernameOrEmail(e.target.value)}
                  required
                  disabled={isLoading}
                />
              </div>
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : null}
                Send Code
              </Button>
            </form>
          )}

          {step === "verify" && (
            <form onSubmit={handleVerifyCode} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="code">6-Digit Verification Code</Label>
                <Input
                  id="code"
                  type="text"
                  maxLength={6}
                  placeholder="000000"
                  className="text-center text-lg tracking-widest"
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                  required
                  disabled={isLoading}
                />
              </div>
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : null}
                Verify Code
              </Button>
            </form>
          )}

          {step === "reset" && (
            <form onSubmit={handleResetPassword} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="newPassword">New Password</Label>
                <Input
                  id="newPassword"
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                  disabled={isLoading}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="confirmPassword">Confirm Password</Label>
                <Input
                  id="confirmPassword"
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                  disabled={isLoading}
                />
              </div>
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : null}
                Reset Password
              </Button>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default ForgotPassword;
