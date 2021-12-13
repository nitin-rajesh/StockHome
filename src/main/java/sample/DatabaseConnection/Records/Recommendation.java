package sample.DatabaseConnection.Records;

public record Recommendation(String tradeName, int recID, String userID, String date, long quantity, double riskFactor) {

}
