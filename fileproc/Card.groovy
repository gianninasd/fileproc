// @author Dimitrios Gianninas
// card object
class Card {
  String cardNum
  CardExpiry cardExpiry

  String toString() {
    return "Card[xxxx" + cardNum.substring(12) + "]"
  }
}