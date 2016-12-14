package platform.beam.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Custom Beam API channels endpoint class
 * Created by keesh on 12/12/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BeamChannel {

    /**
     * The unique ID of the channel
     */
    private Integer id;
    /**
     * The ID of the user owning the channel
     */
    private Integer userId;
    /**
     * The name and url of the channel
     */
    private String token;
    /**
     * Indicated if the channel is active
     */
    private Boolean online;
    /**
     * Indicates if the channel is featured
     */
    private Boolean featured;
    /**
     * Indicates if the channel is partnered
     */
    private Boolean partnered;
    /**
     * The ID of the transcoding profile
     */
    private Integer transcodingProfileId;
    /**
     * Indicates if the channel is suspended
     */
    private Boolean suspended;
    /**
     * The title of the channel
     */
    private String name;
    /**
     * The target audience of the channel
     */
    private String audience;
    /**
     * Amount of unique viewers that ever viewed the channel
     */
    private Integer viewersTotal;
    /**
     * Amount of current viewers
     */
    private Integer viewersCurrent;
    /**
     * Amount of followers
     */
    private Integer numFollowers;
    /**
     * The description of the channel (can contain HTML)
     */
    private String description;
    /**
     * The ID of the game type
     */
    private Integer typeId;
    /**
     * Indicates if that channel is interactive
     */
    private Boolean interactive;
    /**
     * The ID of the interactive game used
     */
    private Integer interactiveGameId;
    /**
     * The FTL stream ID
     */
    private Integer ftl;
    /**
     * Indicates if the channel has VOD saved
     */
    private Boolean hasVod;
    /**
     * ISO-639 language ID
     */
    private String languageId;
    /**
     * The ID of the cover resource
     */
    private Integer coverId;
    /**
     * The resource ID of the thumbnail
     */
    private Integer thumbnailId;
    /**
     * Indicates if the channel has VOD recording enabled
     */
    private Boolean vodEnabled;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    /**
     * Allows access to the Type resource
     */
    private Type type;
    /**
     * Allows access to the User resource
     */
    private User user;
    /**
     * Allows access to the Thumbnail resource
     */
    private Thumbnail thumbnail;
    /**
     * Allows access to the Cover resource
     */
    private Cover cover;
    /**
     * Allows access to the Badge resource
     */
    private Badge badge;
    /**
     * Allows access to the Preferences resource
     */
    private Preferences preferences;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Boolean getPartnered() {
        return partnered;
    }

    public void setPartnered(Boolean partnered) {
        this.partnered = partnered;
    }

    public Integer getTranscodingProfileId() {
        return transcodingProfileId;
    }

    public void setTranscodingProfileId(Integer transcodedProfileId) {
        this.transcodingProfileId = transcodedProfileId;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Integer getViewersTotal() {
        return viewersTotal;
    }

    public void setViewersTotal(Integer viewersTotal) {
        this.viewersTotal = viewersTotal;
    }

    public Integer getViewersCurrent() {
        return viewersCurrent;
    }

    public void setViewersCurrent(Integer viewersCurrent) {
        this.viewersCurrent = viewersCurrent;
    }

    public Integer getNumFollowers() {
        return numFollowers;
    }

    public void setNumFollowers(Integer numFollowers) {
        this.numFollowers = numFollowers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Boolean getInteractive() {
        return interactive;
    }

    public void setInteractive(Boolean interactive) {
        this.interactive = interactive;
    }

    public Integer getInteractiveGameId() {
        return interactiveGameId;
    }

    public void setInteractiveGameId(Integer interactiveGameId) {
        this.interactiveGameId = interactiveGameId;
    }

    public Integer getFtl() {
        return ftl;
    }

    public void setFtl(Integer ftl) {
        this.ftl = ftl;
    }

    public Boolean getHasVod() {
        return hasVod;
    }

    public void setHasVod(Boolean hasVod) {
        this.hasVod = hasVod;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public Integer getCoverId() {
        return coverId;
    }

    public void setCoverId(Integer coverId) {
        this.coverId = coverId;
    }

    public Integer getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(Integer thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public Boolean getVodEnabled() {
        return vodEnabled;
    }

    public void setVodEnabled(Boolean vodEnabled) {
        this.vodEnabled = vodEnabled;
    }

    /**
     * With Beam, types are games.
     */
    public static class Type {

        /**
         * The unique ID of the game type
         */
        private Integer id;
        /**
         * The name of the type
         */
        private String name;
        /**
         * The URL to the types cover
         */
        private String coverUrl;
        /**
         * The name of the parent type
         */
        private String parent;
        /**
         * The description of the type
         */
        private String description;
        /**
         * The source where the type has been imported from
         */
        private String source;
        /**
         * Total amount of users watching this type of stream
         */
        private Integer viewersCurrent;
        /**
         * Amount of streams online with this type
         */
        private Integer online;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Integer getViewersCurrent() {
            return viewersCurrent;
        }

        public void setViewersCurrent(Integer viewersCurrent) {
            this.viewersCurrent = viewersCurrent;
        }

        public Integer getOnline() {
            return online;
        }

        public void setOnline(Integer online) {
            this.online = online;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {

        /**
         * The unique ID of the user
         */
        private Integer id;
        /**
         * The users experience level, related to experience
         */
        private Integer level;
        /**
         * The ID of the user's main team.
         */
        private Integer primaryTeam;
        /**
         * The users name
         * > minLength 4
         * > maxLength 20
         * > pattern ^[A-Za-z_][\w-]+$
         */
        private String username;
        /**
         * Indicates whether the user has verified their email
         */
        private Boolean verified;
        /**
         * The user's experience points
         */
        private Integer experience;
        /**
         * The amount of sparks the user has
         */
        private Integer sparks;
        /**
         * The user's profile URL
         */
        private String avatarUrl;
        /**
         * The user's biography, may contain HTML
         */
        private String bio;
        /**
         * The ID of the transcoding profile currently active
         */
        private Integer transcodingProfileId;
        /**
         * Indicates whether the user can choose a transcode profile or not
         */
        private Boolean hasTranscodes;
        private Social social;

        public Integer getPrimaryTeam() {
            return primaryTeam;
        }

        public void setPrimaryTeam(Integer primaryTeam) {
            this.primaryTeam = primaryTeam;
        }

        public Social getSocial() {
            return social;
        }

        public void setSocial(Social social) {
            this.social = social;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Boolean getVerified() {
            return verified;
        }

        public void setVerified(Boolean verified) {
            this.verified = verified;
        }

        public Integer getExperience() {
            return experience;
        }

        public void setExperience(Integer experience) {
            this.experience = experience;
        }

        public Integer getSparks() {
            return sparks;
        }

        public void setSparks(Integer sparks) {
            this.sparks = sparks;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public Integer getTranscodingProfileId() {
            return transcodingProfileId;
        }

        public void setTranscodingProfileId(Integer transcodingProfileId) {
            this.transcodingProfileId = transcodingProfileId;
        }

        public Boolean getHasTranscodes() {
            return hasTranscodes;
        }

        public void setHasTranscodes(Boolean hasTranscodes) {
            this.hasTranscodes = hasTranscodes;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(level).append(social).append(id).append(username).append(verified)
                    .append(experience).append(sparks).append(avatarUrl).append(bio).append(primaryTeam).toHashCode();
        }

        @Override

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof User)) {
                return false;
            }
            User rhs = ((User) other);
            return new EqualsBuilder().append(level, rhs.level).append(social, rhs.social).append(id, rhs.id)
                    .append(username, rhs.username).append(verified, rhs.verified).append(experience, rhs.experience)
                    .append(sparks, rhs.sparks).append(avatarUrl, rhs.avatarUrl).append(bio, rhs.bio)
                    .append(primaryTeam, rhs.primaryTeam).isEquals();
        }

        public static class Social {

            /**
             * Twitter profile URL
             */
            private String twitter;
            /**
             * Facebook profile URL
             */
            private String facebook;
            /**
             * YouTube profile URL
             */
            private String youtube;
            /**
             * Player.me profile URL
             */
            private String player;
            /**
             * Discord username and discriminator
             */
            private String discord;
            /**
             * A list of social keys which have been verified via lining the Beam account with the account on the
             * corresponding external service
             */
            private String[] verified;

            public String getTwitter() {
                return twitter;
            }

            public void setTwitter(String twitter) {
                this.twitter = twitter;
            }

            public String getFacebook() {
                return facebook;
            }

            public void setFacebook(String facebook) {
                this.facebook = facebook;
            }

            public String getYoutube() {
                return youtube;
            }

            public void setYoutube(String youtube) {
                this.youtube = youtube;
            }

            public String getPlayer() {
                return player;
            }

            public void setPlayer(String player) {
                this.player = player;
            }

            public String getDiscord() {
                return discord;
            }

            public void setDiscord(String discord) {
                this.discord = discord;
            }

            public String[] getVerified() {
                return verified;
            }

            public void setVerified(String[] verified) {
                this.verified = verified;
            }
        }
    }

    public static class Thumbnail {

        /**
         * The unique id of the resource
         */
        private Integer id;
        /**
         * The type fo the resource
         */
        private String type;
        /**
         * ID linking to the parent object
         */
        private Integer relid;
        /**
         * The URL of the resource
         */
        private String url;
        /**
         * The storage type of the resource
         */
        private String store = "s3";
        /**
         * Relative URL to the resource
         */
        private String remotePath;
        /**
         * Additional resource information
         */

        private Object meta;
        private String createdAt;
        private String updatedAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getRelid() {
            return relid;
        }

        public void setRelid(Integer relid) {
            this.relid = relid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
        }

        public String getRemotePath() {
            return remotePath;
        }

        public void setRemotePath(String remotePath) {
            this.remotePath = remotePath;
        }

        public Object getMeta() {
            return meta;
        }

        public void setMeta(Object meta) {
            this.meta = meta;
        }
    }

    public static class Cover {

        /**
         * The unique id of the resource
         */
        private Integer id;
        /**
         * The type fo the resource
         */
        private String type;
        /**
         * ID linking to the parent object
         */
        private Integer relid;
        /**
         * The URL of the resource
         */
        private String url;
        /**
         * The storage type of the resource
         */
        private String store = "s3";
        /**
         * Relative URL to the resource
         */
        private String remotePath;
        /**
         * Additional resource information
         */
        private Object meta;
        private String createdAt;
        private String updatedAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getRelid() {
            return relid;
        }

        public void setRelid(Integer relid) {
            this.relid = relid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
        }

        public String getRemotePath() {
            return remotePath;
        }

        public void setRemotePath(String remotePath) {
            this.remotePath = remotePath;
        }

        public Object getMeta() {
            return meta;
        }

        public void setMeta(Object meta) {
            this.meta = meta;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Badge {

        /**
         * The unique id of the resource
         */
        private Integer id;
        /**
         * The type fo the resource
         */
        private String type;
        /**
         * ID linking to the parent object
         */
        private Integer relid;
        /**
         * The URL of the resource
         */
        private String url;
        /**
         * The storage type of the resource
         */
        private String store = "s3";
        /**
         * Relative URL to the resource
         */
        private String remotePath;
        /**
         * Additional resource information
         */
        private Object meta;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getRelid() {
            return relid;
        }

        public void setRelid(Integer relid) {
            this.relid = relid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getStore() {
            return store;
        }

        public void setStore(String store) {
            this.store = store;
        }

        public String getRemotePath() {
            return remotePath;
        }

        public void setRemotePath(String remotePath) {
            this.remotePath = remotePath;
        }

        public Object getMeta() {
            return meta;
        }

        public void setMeta(Object meta) {
            this.meta = meta;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Preferences {

        /**
         * Specifies who is allowed to co-stream.
         */
        private String costreamAllow;
        /**
         * The text used when sharing the stream. The template parameter %URL% will be replaced with the share url.
         * The template parameter %USER% will be replaced with the sharers name.
         */
        private String sharetext;
        /**
         * Specified whether links are allowed in the chat.
         */
        private Boolean channelLinksAllowed;
        /**
         * Specifies whether links are clickable in the chat.
         */
        private Boolean channelLinksClickable;
        /**
         * Interval required between each chat message.
         */
        private Integer channelSlowchat;
        /**
         * The message to be used when a user subscribed to the channel. The template parameter %USER% will be
         * replaced with the subscribers name.
         */
        private String channelNotifySubscribemessage;
        /**
         * Indicates whether a notification should be shown upon subscription.
         */
        private Boolean channelNotifySubscribe;
        /**
         * The message to be used when a user followed the channel. The template parameter "%USER%" will be replaced
         * with the followers name.
         */
        private String channelNotifyFollowmessage;
        /**
         * Indicated whether a notification should be shown upon follow.
         */
        private Boolean channelNotifyFollow;
        /**
         * The text to be added to the subscription email.
         */
        private String channelPartnerSubmail;
        /**
         * Indicates whether to mute when the streamer opens his own stream.
         */
        private Boolean channelPlayerMuteOwn;
        /**
         * Indicates whether the tweet button should be shown.
         */
        private Boolean channelTweetEnabled;
        /**
         * The message to be used when a user tweets about the channel. The template parameter %URL% will be replaced
         * with the share url.
         */
        private String channelTweetBody;

        public String getCostreamAllow() {
            return costreamAllow;
        }

        public void setCostreamAllow(String costreamAllow) {
            this.costreamAllow = costreamAllow;
        }

        public String getSharetext() {
            return sharetext;
        }

        public void setSharetext(String sharetext) {
            this.sharetext = sharetext;
        }

        public Boolean getChannelLinksAllowed() {
            return channelLinksAllowed;
        }

        public void setChannelLinksAllowed(Boolean channelLinksAllowed) {
            this.channelLinksAllowed = channelLinksAllowed;
        }

        public Boolean getChannelLinksClickable() {
            return channelLinksClickable;
        }

        public void setChannelLinksClickable(Boolean channelLinksClickable) {
            this.channelLinksClickable = channelLinksClickable;
        }

        public Integer getChannelSlowchat() {
            return channelSlowchat;
        }

        public void setChannelSlowchat(Integer channelSlowchat) {
            this.channelSlowchat = channelSlowchat;
        }

        public String getChannelNotifySubscribemessage() {
            return channelNotifySubscribemessage;
        }

        public void setChannelNotifySubscribemessage(String channelNotifySubscribemessage) {
            this.channelNotifySubscribemessage = channelNotifySubscribemessage;
        }

        public Boolean getChannelNotifySubscribe() {
            return channelNotifySubscribe;
        }

        public void setChannelNotifySubscribe(Boolean channelNotifySubscribe) {
            this.channelNotifySubscribe = channelNotifySubscribe;
        }

        public String getChannelNotifyFollowmessage() {
            return channelNotifyFollowmessage;
        }

        public void setChannelNotifyFollowmessage(String channelNotifyFollowmessage) {
            this.channelNotifyFollowmessage = channelNotifyFollowmessage;
        }

        public Boolean getChannelNotifyFollow() {
            return channelNotifyFollow;
        }

        public void setChannelNotifyFollow(Boolean channelNotifyFollow) {
            this.channelNotifyFollow = channelNotifyFollow;
        }

        public String getChannelPartnerSubmail() {
            return channelPartnerSubmail;
        }

        public void setChannelPartnerSubmail(String channelPartnerSubmail) {
            this.channelPartnerSubmail = channelPartnerSubmail;
        }

        public Boolean getChannelPlayerMuteOwn() {
            return channelPlayerMuteOwn;
        }

        public void setChannelPlayerMuteOwn(Boolean channelPlayerMuteOwn) {
            this.channelPlayerMuteOwn = channelPlayerMuteOwn;
        }

        public Boolean getChannelTweetEnabled() {
            return channelTweetEnabled;
        }

        public void setChannelTweetEnabled(Boolean channelTweetEnabled) {
            this.channelTweetEnabled = channelTweetEnabled;
        }

        public String getChannelTweetBody() {
            return channelTweetBody;
        }

        public void setChannelTweetBody(String channelTweetBody) {
            this.channelTweetBody = channelTweetBody;
        }
    }
}
