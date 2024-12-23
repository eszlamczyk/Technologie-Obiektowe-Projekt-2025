package monaditto.cinemafront.response;

public record ResponseResult(
   int statusCode,
   String body
) {}
