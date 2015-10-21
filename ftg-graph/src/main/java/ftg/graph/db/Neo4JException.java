package ftg.graph.db;

public class Neo4JException extends RuntimeException {
    public Neo4JException() {
        super();
    }

    public Neo4JException(String message) {
        super(message);
    }

    public Neo4JException(String message, Throwable cause) {
        super(message, cause);
    }

    public Neo4JException(Throwable cause) {
        super(cause);
    }
}
