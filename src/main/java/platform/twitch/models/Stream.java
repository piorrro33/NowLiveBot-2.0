
package platform.twitch.models;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "_id",
    "game",
    "community_id",
    "viewers",
    "video_height",
    "average_fps",
    "delay",
    "created_at",
    "is_playlist",
    "preview",
    "channel"
})
public class Stream {

    @JsonProperty("_id")
    private Long id;
    @JsonProperty("game")
    private String game;
    @JsonProperty("community_id")
    private String communityId;
    @JsonProperty("viewers")
    private Integer viewers;
    @JsonProperty("video_height")
    private Integer videoHeight;
    @JsonProperty("average_fps")
    private Float averageFps;
    @JsonProperty("delay")
    private Integer delay;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("is_playlist")
    private Boolean isPlaylist;
    @JsonProperty("preview")
    private Preview preview;
    @JsonProperty("channel")
    private Channel channel;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Stream() {
    }

    /**
     * 
     * @param id
     * @param videoHeight
     * @param viewers
     * @param preview
     * @param averageFps
     * @param createdAt
     * @param isPlaylist
     * @param delay
     * @param game
     * @param channel
     * @param communityId
     */
    public Stream(Long id, String game, String communityId, Integer viewers, Integer videoHeight, Float averageFps, Integer delay, String createdAt, Boolean isPlaylist, Preview preview, Channel channel) {
        super();
        this.id = id;
        this.game = game;
        this.communityId = communityId;
        this.viewers = viewers;
        this.videoHeight = videoHeight;
        this.averageFps = averageFps;
        this.delay = delay;
        this.createdAt = createdAt;
        this.isPlaylist = isPlaylist;
        this.preview = preview;
        this.channel = channel;
    }

    @JsonProperty("_id")
    public Long getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("game")
    public String getGame() {
        return game;
    }

    @JsonProperty("game")
    public void setGame(String game) {
        this.game = game;
    }

    @JsonProperty("community_id")
    public String getCommunityId() {
        return communityId;
    }

    @JsonProperty("community_id")
    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    @JsonProperty("viewers")
    public Integer getViewers() {
        return viewers;
    }

    @JsonProperty("viewers")
    public void setViewers(Integer viewers) {
        this.viewers = viewers;
    }

    @JsonProperty("video_height")
    public Integer getVideoHeight() {
        return videoHeight;
    }

    @JsonProperty("video_height")
    public void setVideoHeight(Integer videoHeight) {
        this.videoHeight = videoHeight;
    }

    @JsonProperty("average_fps")
    public Float getAverageFps() {
        return averageFps;
    }

    @JsonProperty("average_fps")
    public void setAverageFps(Float averageFps) {
        this.averageFps = averageFps;
    }

    @JsonProperty("delay")
    public Integer getDelay() {
        return delay;
    }

    @JsonProperty("delay")
    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("is_playlist")
    public Boolean getIsPlaylist() {
        return isPlaylist;
    }

    @JsonProperty("is_playlist")
    public void setIsPlaylist(Boolean isPlaylist) {
        this.isPlaylist = isPlaylist;
    }

    @JsonProperty("preview")
    public Preview getPreview() {
        return preview;
    }

    @JsonProperty("preview")
    public void setPreview(Preview preview) {
        this.preview = preview;
    }

    @JsonProperty("channel")
    public Channel getChannel() {
        return channel;
    }

    @JsonProperty("channel")
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(game).append(communityId).append(viewers).append(videoHeight).append(averageFps).append(delay).append(createdAt).append(isPlaylist).append(preview).append(channel).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Stream) == false) {
            return false;
        }
        Stream rhs = ((Stream) other);
        return new EqualsBuilder().append(id, rhs.id).append(game, rhs.game).append(communityId, rhs.communityId).append(viewers, rhs.viewers).append(videoHeight, rhs.videoHeight).append(averageFps, rhs.averageFps).append(delay, rhs.delay).append(createdAt, rhs.createdAt).append(isPlaylist, rhs.isPlaylist).append(preview, rhs.preview).append(channel, rhs.channel).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
