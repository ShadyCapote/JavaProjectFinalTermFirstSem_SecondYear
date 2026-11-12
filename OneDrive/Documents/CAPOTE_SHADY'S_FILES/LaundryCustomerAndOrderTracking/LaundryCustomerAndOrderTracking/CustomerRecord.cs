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
    public partial class CustomerRecord : Form   
    {
        private DataTable customerTable;
        public CustomerRecord()
        {
            InitializeComponent();
        }

        private void btnAdd_Click(object sender, EventArgs e)
        {
            string name = ShowInput("Enter customer name:", "Add Customer");
            if (string.IsNullOrWhiteSpace(name)) return;

            string contact = ShowInput("Enter contact number:", "Add Customer");
            string address = ShowInput("Enter address:", "Add Customer");

            int newId = customerTable.Rows.Count == 0 ? 1 :
                Convert.ToInt32(customerTable.Compute("MAX(CustomerID)", "")) + 1;

            customerTable.Rows.Add(newId, name, contact, address);
        }

        private static string ShowInput(string promt, string title)
        {
            using (Form dialog = new Form())
            using (Label lbl = new Label())
            using (TextBox txt = new TextBox())
            using (Button ok = new Button())
            {
                dialog.Text = title;
                dialog.FormBorderStyle = FormBorderStyle.FixedDialog;
                dialog.StartPosition = FormStartPosition.CenterParent;
                dialog.ClientSize = new Size(360, 130);
                dialog.MaximizeBox = false;
                dialog.MinimizeBox = false;

                lbl.Text = promt;
                lbl.Location = new Point(12, 15);
                lbl.AutoSize = true;

                txt.Location = new Point(15, 45);
                txt.Size = new Size(330, 27);

                ok.Text = "OK";
                ok.DialogResult = DialogResult.OK;
                ok.Location = new Point(255, 85);
                ok.Size = new Size(90, 30);

                dialog.Controls.Add(lbl);
                dialog.Controls.Add(txt);
                dialog.Controls.Add(ok);
                dialog.AcceptButton = ok;

                return dialog.ShowDialog() == DialogResult.OK ? txt.Text.Trim() : "";

            }
        }

        private void CustomerRecord_Load(object sender, EventArgs e)
        { 

            customerTable = new DataTable();
        customerTable.Columns.Add("CustomerID", typeof(int));
            customerTable.Columns.Add("Name", typeof(string));
            customerTable.Columns.Add("Contact", typeof(string));
            customerTable.Columns.Add("Address", typeof(string));

            // Sample data. Replace with data from your database.
            customerTable.Rows.Add(1, "Juan Dela Cruz", "0917-000-0001", "Quezon City");
            customerTable.Rows.Add(2, "Maria Santos", "0917-000-0002", "Manila");

            dgvCustomers.DataSource = customerTable;
            this.AcceptButton = btnSearch;
            
        }

        private void btnSearch_Click(object sender, EventArgs e)
        {
            string keyword = txtSearch.Text.Trim().Replace("'", "''");

            if (string.IsNullOrEmpty(keyword))
            {
                customerTable.DefaultView.RowFilter = "";
                return;
            }

            customerTable.DefaultView.RowFilter =
                $"Name LIKE '%{keyword}%' OR Contact LIKE '%{keyword}%'";

        }

        private void btnDashboard_Click(object sender, EventArgs e)
        {
            Dashboard dashboard = new Dashboard();
            dashboard.Show();
            this.Close();
        }

        private void btnDashboard_Click_1(object sender, EventArgs e)
        {
            Dashboard dashboard = new Dashboard();
            dashboard.Show();
            this.Close();
        }

        private void btnService_Click(object sender, EventArgs e)
        {
            Service_Pricing service = new Service_Pricing();
            service.Show();
            this.Close();
        }

        private void btnOrderTracking_Click(object sender, EventArgs e)
        {
            Order_Tracking orderTracking = new Order_Tracking();
            orderTracking.Show();
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
    }


}

