using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Shapes;

namespace Monopoly
{
    enum Action { Chance, Chest, GoToJail, Railroad, Utility, None }

    class Cell
    {
        bool draw;
        int row, col;
        public int timesLanded = 0;
        Grid gameGrid;
        Rectangle rect;
        Action action;
        Random random;

        public Cell(int row, int col, Grid gameGrid, Action action, double probability, bool draw)
        {
            random = new Random();
            this.row = row;
            this.col = col;
            this.gameGrid = gameGrid;
            this.action = action;
            this.draw = draw;
            if (!draw)
                return;

            rect = new Rectangle();
            var txt = new TextBlock();
            txt.Background = Brushes.White;
            txt.FontWeight = FontWeight.FromOpenTypeWeight(500);
            txt.TextAlignment = TextAlignment.Center;
            txt.VerticalAlignment = VerticalAlignment.Top;
            txt.Text = probability.ToString("N3");
            txt.Opacity = 0.8;
            SetToGridCell(new List<UIElement> { rect, txt });
        }

        private void SetToGridCell(List<UIElement> elements)
        {
            foreach (var e in elements)
            {
                Grid.SetRow(e, row);
                Grid.SetColumn(e, col);
                gameGrid.Children.Add(e);
            }
        }
        
        public void ColorCell()
        {
            if (!draw)
                return;
            rect.Fill = Brushes.Black;
            rect.Opacity = 0.50;
        }

        public void UncolorCell()
        {
            if (!draw)
                return;
            rect.Fill = Brushes.Transparent;
            rect.Opacity = 0;
        }

        public Action getAction()
        {
            return action;
        }

        public void DoCellAction(Player p)
        {
            switch(action)
            {
                case Action.Chance:
                    Console.WriteLine("Chance!");
                    int card = (int)(random.NextDouble() * 16) + 1;
                    switch (card)
                    {
                        case 1:
                            Console.WriteLine("Received get out of jail free card!");
                            p.ReceiveGetOutOfJailFreeCard();
                            break;
                        case 2:
                            Console.WriteLine("Move to Illinois Avenue!");
                            p.MoveTo(Place.IllinoisAve);
                            break;
                        case 3:
                            Console.WriteLine("Move to ST Charles Place!");
                            p.MoveTo(Place.CharlesPlc);
                            break;
                        case 4:
                            Console.WriteLine("Move to start!");
                            p.MoveTo(Place.GO);
                            break;
                        case 5:
                            Console.WriteLine("Move to nearest utility!");
                            p.MoveTo(Place.NearestUtility);
                            break;
                        case 6:
                            Console.WriteLine("Move 3 spaces back!");
                            p.MoveTo(Place.ThreeSpacesBack);
                            break;
                        case 7:
                            Console.WriteLine("Move to nearest railroad!");
                            p.MoveTo(Place.NearestRailroad);
                            break;
                        case 8:
                            Console.WriteLine("Move to reading railroad!");
                            p.MoveTo(Place.ReadingRailroad);
                            break;
                        case 9:
                            Console.WriteLine("move to boardwalk!");
                            p.MoveTo(Place.Boardwalk);
                            break;
                        case int i when (i >= 10 && i <= 12):
                            Console.WriteLine("Pay money!");
                            //pay money
                            break;
                        case int i when (i >= 13 && i <= 15):
                            Console.WriteLine("Collect money!");
                            //collect money
                            break;
                        case 16:
                            Console.WriteLine("Go to jail!");
                            p.MoveTo(Place.Jail);
                            break;
                    }
                    break;
                case Action.Chest:
                    Console.WriteLine("Chest!");
                    int ccard = (int)(random.NextDouble() * 16) + 1;
                    switch (ccard)
                    {
                        case 1:
                            Console.WriteLine("Received get out of jail free card!");
                            p.ReceiveGetOutOfJailFreeCard();
                            break;
                        case 2:
                            Console.WriteLine("Move to start!");
                            p.MoveTo(Place.GO);
                            break;
                        case int i when (i >= 3 && i <= 6):
                            Console.WriteLine("Pay money!");
                            //pay money
                            break;
                        case int i when (i >= 7 && i <= 15):
                            Console.WriteLine("Collect money!");
                            //collect money
                            break;
                        case 16:
                            Console.WriteLine("Go to jail!");
                            p.MoveTo(Place.Jail);
                            break;
                    }
                    break;
                case Action.GoToJail:
                    Console.WriteLine("Go to jail!");
                    p.MoveTo(Place.Jail);
                    break;
            }
        }
    }
}
