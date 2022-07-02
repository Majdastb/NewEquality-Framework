public class StandardResponse {
    private StatusResponse status;

    public StandardResponse(StatusResponse status) {
        this.status = status;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public void setStatus(StatusResponse status) {
        this.status = status;
    }

}