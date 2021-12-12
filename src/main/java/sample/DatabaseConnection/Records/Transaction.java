package sample.DatabaseConnection.Records;

public record Transaction(double price, String timeStamp, int transactionID, char buyOrSell, long quantity, String tradeName, String userID) {
}
