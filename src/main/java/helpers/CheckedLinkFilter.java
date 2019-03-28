package helpers;

import java.time.LocalDateTime;

public interface CheckedLinkFilter {
    public Integer getStatus();
    public LocalDateTime getCheckedBeforeDate();
    public LocalDateTime getCheckedAfterDate();

}
