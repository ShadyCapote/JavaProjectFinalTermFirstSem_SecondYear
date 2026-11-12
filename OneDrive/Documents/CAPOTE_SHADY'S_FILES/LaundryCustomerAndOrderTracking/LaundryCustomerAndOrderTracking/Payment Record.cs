using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace LaundryCustomerAndOrderTracking
{
    public partial class Payment_Record : Form
    {
        public Payment_Record()
        {
            InitializeComponent();
        }

        private void btnOrderTracking_Click(object sender, EventArgs e)
        {
            Order_Tracking orderTracking = new Order_Tracking();    
            orderTracking.Show();
            this.Close();
        }

        private void btnDashboard_Click(object sender, EventArgs e)
        {
            Dashboard   dashboard = new Dashboard();
            dashboard.Show();
            this.Close();
        }

        private void btnService_Click(object sender, EventArgs e)
        {
            Service_Pricing service = new Service_Pricing();    
            service.Show();
            this.Close();
        }

        private void btnPayment_Click(object sender, EventArgs e)
        {
            Payment_Record payment = new Payment_Record(); 
            payment.Show();
            this.Close();
        }

        private void btnReports_Click(object sender, EventArgs e)
        {
            Reports reports = new Reports();
            reports.Show();
            this.Close();
        }

        private void btnCustomer_Click(object sender, EventArgs e)
        {
            CustomerRecord customerRecord = new CustomerRecord();
            customerRecord.Show();
            this.Close();
        }
    }
}
