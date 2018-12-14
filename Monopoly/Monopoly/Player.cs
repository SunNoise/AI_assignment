using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Monopoly
{
    enum Place { Jail, IllinoisAve, CharlesPlc, GO, NearestUtility, ThreeSpacesBack, NearestRailroad, ReadingRailroad, Boardwalk }
    class Player
    {
        Random random;
        Dice dice;
        LinkedList<Cell> board;
        LinkedListNode<Cell> current;
        bool hasGetOutOfJailFreeCard = false;
        int inJail = 0;
        internal int money = 0;

        public Player(LinkedList<Cell> board, Dice dice)
        {
            random = new Random();
            this.dice = dice;
            this.board = board;
            current = board.First;
        }

        public void ReceiveGetOutOfJailFreeCard()
        {
            hasGetOutOfJailFreeCard = true;
        }

        public void UseGetOutOfJailFreeCard()
        {
            hasGetOutOfJailFreeCard = false;
            inJail = -1;
        }

        public void DoCellAction()
        {
            current.Value.timesLanded++;
            current.Value.DoCellAction(this);
        }

        public void MoveNumberOfSteps(int steps, bool back = false)
        {
            current.Value.UncolorCell();
            for(int i = 0; i < steps; i++)
            {
                if(back)
                    current = current.Previous ?? current.List.Last;
                else
                    current = current.Next ?? current.List.First;
            }
            current.Value.ColorCell();
            DoCellAction();
        }

        private void MoveUntil(Action action)
        {
            while(current.Value.getAction() != action)
                current = current.Next ?? current.List.First;
            current.Value.ColorCell();
            DoCellAction();
        }

        public void MoveTo(Place place)
        {
            current.Value.UncolorCell();
            switch (place)
            {
                case Place.Jail:
                    current = board.First;
                    MoveNumberOfSteps(10);
                    inJail = 3;
                    break;
                case Place.GO:
                    current = board.First;
                    current.Value.ColorCell();
                    break;
                case Place.Boardwalk:
                    current = board.First;
                    MoveNumberOfSteps(1,true);
                    break;
                case Place.CharlesPlc:
                    current = board.First;
                    MoveNumberOfSteps(11);
                    break;
                case Place.IllinoisAve:
                    current = board.First;
                    MoveNumberOfSteps(24);
                    break;
                case Place.NearestRailroad:
                    current = board.First;
                    MoveUntil(Action.Railroad);
                    break;
                case Place.NearestUtility:
                    current = board.First;
                    MoveUntil(Action.Utility);
                    break;
                case Place.ReadingRailroad:
                    current = board.First;
                    MoveNumberOfSteps(5);
                    break;
                case Place.ThreeSpacesBack:
                    current = board.First;
                    MoveNumberOfSteps(3, true);
                    break;
                default:
                    Console.WriteLine("No such place exists");
                    break;
            }
            DoCellAction();
        }

        public void Turn(int roll)
        {
            inJail--;
            if (inJail > 0 && hasGetOutOfJailFreeCard)
                UseGetOutOfJailFreeCard();
            if (roll > 2)
            {
                MoveTo(Place.Jail);
                return;
            }

            int die1 = RollDice();
            int die2 = RollDice();

            dice.Update(die1, die2);

            int sum = die1 + die2;
            bool doubles = die1 == die2;

            if (inJail > 0 && !doubles)
                return;

            MoveNumberOfSteps(sum);

            if (inJail < 0 && doubles)
            {
                Console.WriteLine($"Got doubles ({die1},{die2}), re-rolling!");
                Turn(roll++);
            }
        }

        private int RollDice()
        {
            int roll = (int)(random.NextDouble() * 6) + 1;
            return roll;
        }
    }
}
