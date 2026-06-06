import { ShopSupplyResponseDTO } from '@/services/shop-supply.service';

export const printBill = (delivery: ShopSupplyResponseDTO) => {
  const printWindow = window.open('', '_blank');
  if (!printWindow) return;

  const billHTML = `
    <!DOCTYPE html>
    <html>
    <head>
      <title>Delivery Bill - ${delivery.supplyId}</title>
      <style>
        body {
          font-family: Arial, sans-serif;
          margin: 20px;
          font-size: 12px;
        }
        .header {
          text-align: center;
          margin-bottom: 20px;
          border-bottom: 2px solid #333;
          padding-bottom: 10px;
        }
        .header h1 {
          margin: 0;
          font-size: 24px;
        }
        .header p {
          margin: 5px 0;
        }
        .info-section {
          margin: 15px 0;
        }
        .info-row {
          display: flex;
          justify-content: space-between;
          margin: 5px 0;
        }
        table {
          width: 100%;
          border-collapse: collapse;
          margin: 20px 0;
        }
        th, td {
          border: 1px solid #ddd;
          padding: 8px;
          text-align: left;
        }
        th {
          background-color: #f2f2f2;
        }
        .total-row {
          font-weight: bold;
          font-size: 14px;
        }
        .footer {
          margin-top: 40px;
          border-top: 2px solid #333;
          padding-top: 10px;
          text-align: center;
        }
        @media print {
          button { display: none; }
        }
      </style>
    </head>
    <body>
      <div class="header">
        <h1>Your Bakery Name</h1>
        <p>Address Line 1, Address Line 2</p>
        <p>Phone: +94 XX XXX XXXX</p>
        <h2>DELIVERY INVOICE</h2>
      </div>

      <div class="info-section">
        <div class="info-row">
          <strong>Invoice #:</strong>
          <span>${delivery.supplyId}</span>
        </div>
        <div class="info-row">
          <strong>Date:</strong>
          <span>${delivery.supplyDate}</span>
        </div>
        <div class="info-row">
          <strong>Shop:</strong>
          <span>${delivery.shopName || 'N/A'}</span>
        </div>
        <div class="info-row">
          <strong>Salesman:</strong>
          <span>${delivery.driverName || 'N/A'}</span>
        </div>
        <div class="info-row">
          <strong>Vehicle:</strong>
          <span>${delivery.vehicleNo || delivery.vehicleId}</span>
        </div>
      </div>

      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Product</th>
            <th>Quantity</th>
            <th>Unit Price (Rs.)</th>
            <th>Total (Rs.)</th>
          </tr>
        </thead>
        <tbody>
          ${delivery.items.map((item, index) => `
            <tr>
              <td>${index + 1}</td>
              <td>${item.productName || 'Product'}</td>
              <td>${item.quantity}</td>
              <td>${item.price?.toFixed(2) || '0.00'}</td>
              <td>${((item.quantity || 0) * (item.price || 0)).toFixed(2)}</td>
            </tr>
          `).join('')}
          <tr class="total-row">
            <td colspan="4" style="text-align: right;">TOTAL:</td>
            <td>Rs. ${Number(delivery.totalAmount || 0).toFixed(2)}</td>
          </tr>
        </tbody>
      </table>

      <div class="footer">
        <p><strong>Received By:</strong> _____________________</p>
        <p><strong>Signature:</strong> _____________________</p>
        <p><strong>Date:</strong> _____________________</p>
        <br>
        <p>Thank you for your business!</p>
      </div>

      <div style="text-align: center; margin-top: 20px;">
        <button onclick="window.print()" style="padding: 10px 20px; font-size: 14px; cursor: pointer;">
          Print Bill
        </button>
        <button onclick="window.close()" style="padding: 10px 20px; font-size: 14px; cursor: pointer; margin-left: 10px;">
          Close
        </button>
      </div>
    </body>
    </html>
  `;

  printWindow.document.write(billHTML);
  printWindow.document.close();
};