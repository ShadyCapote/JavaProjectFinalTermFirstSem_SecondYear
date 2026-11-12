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
    public partial class Reports : Form
    {
        public Reports()
        {
            InitializeComponent();
        }

        private void btnReports_Click(object sender, EventArgs e)
        {
            Reports form = new Reports();   
            form.ShowDialog();
            this.Close();
        }

        private void btnPayment_Click(object sender, EventArgs e)
        {
            Payment_Record  form = new Payment_Record();
            form.ShowDialog();
            this.Close();
        }

        private void btnOrderTracking_Click(object sender, EventArgs e)
        {
            Order_Tracking form = new Order_Tracking(); 
            form.ShowDialog();
            this.Close();
        }

        private void btnService_Click(object sender, EventArgs e)
        {
            Service_Pricing form = new Service_Pricing();
            form.ShowDialog();
            this.Close();
        }

        private void btnCustomer_Click(object sender, EventArgs e)
        {
            CustomerRecord customerRecord = new CustomerRecord();
            customerRecord.ShowDialog();
            this.Close();
        }

        private void btnDashboard_Click(object sender, EventArgs e)
        {
            Dashboard dashboard = new Dashboard();
            dashboard.ShowDialog();
            this.Close();
        }
    }
}
