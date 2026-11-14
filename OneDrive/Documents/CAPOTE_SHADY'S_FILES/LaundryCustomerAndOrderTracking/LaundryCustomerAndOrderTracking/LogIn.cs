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
    public partial class LogIn : Form
    {
        public LogIn()
        {
            InitializeComponent();
        }
        int attemptsLeft = 3;

        private void btnLogin_Click(object sender, EventArgs e)
        {
            string userName = txtUsername.Text;
            string password = txtPassword.Text;

            if (userName == "Admin" && password == "admin123")
            {
                MessageBox.Show("Login Successful!");

               
                Dashboard form = new Dashboard();
                form.Show();
                this.Hide();
            }
            else
            {
                attemptsLeft--;

                MessageBox.Show("Invalid credentials!" + "\nAttempts Left: " + attemptsLeft);
                MessageBox.Show("Access Denied!");

                txtUsername.Clear();
                txtPassword.Clear();

                if (attemptsLeft == 0)
                {
                    MessageBox.Show("No more attempts. System Locked!");

                    btnLogin.Enabled = false;
                    txtUsername.Enabled = false;
                    txtPassword.Enabled = false;
                }
            }
        }
    }
}
    

