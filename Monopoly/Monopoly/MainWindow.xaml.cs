using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Monopoly
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        Random random;
        Player p1;
        Dice dice;

        public MainWindow()
        {
            random  = new Random();
            InitializeComponent();

            double[] simulations = new double[40];
            for (int i = 0; i < 1000; i++)
            {
                var sim = Simulate().GetEnumerator();
                sim.MoveNext();
                for (int j=0; j < 40; j++)
                {
                    simulations[j] += sim.Current.timesLanded;
                    sim.MoveNext();
                }
            }
            var sum = simulations.Sum();
            for (int i = 0; i < 40; i++)
            {
                simulations[i] = simulations[i] / sum;
            }

            var board = InitGame(simulations, true);
            dice = new Dice();
            p1 = new Player(board, dice);
            this.DataContext = dice;
        }

        private LinkedList<Cell> InitGame(double[] probabilities, bool draw = false)
        {
            LinkedList<Cell> cells = new LinkedList<Cell>();
            for (int i = 10; i >= 0; i--)
            {
                var action = Action.None;
                if (i == 3)
                    action = Action.Chance;
                else if (i == 5)
                    action = Action.Railroad;
                else if (i == 8)
                    action = Action.Chest;
                cells.AddLast(new Cell(10, i, gameGrid, action, probabilities[10 - i], draw));
            }
            for (int i = 9; i >= 0; i--)
            {
                var action = Action.None;
                if (i == 8)
                    action = Action.Utility;
                else if (i == 5)
                    action = Action.Railroad;
                else if (i == 3)
                    action = Action.Chest;
                cells.AddLast(new Cell(i, 0, gameGrid, action, probabilities[20 - i], draw));
            }
            for (int i = 1; i <= 10; i++)
            {
                var action = Action.None;
                if (i == 2)
                    action = Action.Chance;
                else if (i == 5)
                    action = Action.Railroad;
                else if (i == 8)
                    action = Action.Utility;
                else if (i == 10)
                    action = Action.GoToJail;
                cells.AddLast(new Cell(0, i, gameGrid, action, probabilities[20 + i], draw));
            }
            for (int i = 1; i < 10; i++)
            {
                var action = Action.None;
                if (i == 3)
                    action = Action.Chest;
                else if (i == 5)
                    action = Action.Railroad;
                else if (i == 6)
                    action = Action.Chance;
                cells.AddLast(new Cell(i, 10, gameGrid, action, probabilities[30 + i], draw));
            }

            return cells;
        }

        private void btnRoll_Click(object sender, RoutedEventArgs e)
        {
            p1.Turn(1);
        }

        private LinkedList<Cell> Simulate()
        {
            var board = InitGame(new double[40]);
            var dice = new Dice();
            Player p = new Player(board, dice);
            for(int i = 0; i < 100; i++)
            {
                p.Turn(1);
            }
            return board;
        }
    }
}
