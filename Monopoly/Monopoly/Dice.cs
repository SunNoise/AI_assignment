using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Monopoly
{
    public class Dice : INotifyPropertyChanged
    {
        private ImageSource _imgDie1;
        public ImageSource imgDie1
        {
            get { return _imgDie1; }
            set
            {
                _imgDie1 = value;
                OnPropertyChanged("imgDie1");
            }
        }

        private ImageSource _imgDie2;
        public ImageSource imgDie2
        {
            get { return _imgDie2; }
            set
            {
                _imgDie2 = value;
                OnPropertyChanged("imgDie2");
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        private void ChangeImage(int die, string path)
        {
            Uri imageUri = new Uri(path, UriKind.Relative);
            BitmapImage imageBitmap = new BitmapImage(imageUri);
            if (die == 1)
                imgDie1 = imageBitmap;
            else
                imgDie2 = imageBitmap;
        }

        private static string GetDieImage(int number)
        {
            return $"img/die/{number}.png";
        }

        public void Update(int die1, int die2)
        {
            ChangeImage(1, GetDieImage(die1));
            ChangeImage(2, GetDieImage(die2));
        }

        protected void OnPropertyChanged(string name)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (handler != null)
            {
                handler(this, new PropertyChangedEventArgs(name));
            }
        }

    }
}
