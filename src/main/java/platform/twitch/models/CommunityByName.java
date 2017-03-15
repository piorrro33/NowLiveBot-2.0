/*
 * Copyright 2016-2017 Ague Mort of Veteran Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
        "owner_id",
        "name",
        "summary",
        "description",
        "description_html",
        "rules",
        "rules_html",
        "language",
        "avatar_image_url",
        "cover_image_url"
})
public class CommunityByName {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("owner_id")
    private String ownerId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("summary")
    private String summary;
    @JsonProperty("description")
    private String description;
    @JsonProperty("description_html")
    private String descriptionHtml;
    @JsonProperty("rules")
    private String rules;
    @JsonProperty("rules_html")
    private String rulesHtml;
    @JsonProperty("language")
    private String language;
    @JsonProperty("avatar_image_url")
    private String avatarImageUrl;
    @JsonProperty("cover_image_url")
    private String coverImageUrl;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public CommunityByName() {
    }

    /**
     *
     * @param summary
     * @param id
     * @param description
     * @param ownerId
     * @param name
     * @param avatarImageUrl
     * @param language
     * @param coverImageUrl
     * @param rulesHtml
     * @param rules
     * @param descriptionHtml
     */
    public CommunityByName(String id, String ownerId, String name, String summary, String description, String descriptionHtml, String rules, String rulesHtml, String language, String avatarImageUrl, String coverImageUrl) {
        super();
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.summary = summary;
        this.description = description;
        this.descriptionHtml = descriptionHtml;
        this.rules = rules;
        this.rulesHtml = rulesHtml;
        this.language = language;
        this.avatarImageUrl = avatarImageUrl;
        this.coverImageUrl = coverImageUrl;
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("owner_id")
    public String getOwnerId() {
        return ownerId;
    }

    @JsonProperty("owner_id")
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("summary")
    public String getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("description_html")
    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    @JsonProperty("description_html")
    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    @JsonProperty("rules")
    public String getRules() {
        return rules;
    }

    @JsonProperty("rules")
    public void setRules(String rules) {
        this.rules = rules;
    }

    @JsonProperty("rules_html")
    public String getRulesHtml() {
        return rulesHtml;
    }

    @JsonProperty("rules_html")
    public void setRulesHtml(String rulesHtml) {
        this.rulesHtml = rulesHtml;
    }

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("avatar_image_url")
    public String getAvatarImageUrl() {
        return avatarImageUrl;
    }

    @JsonProperty("avatar_image_url")
    public void setAvatarImageUrl(String avatarImageUrl) {
        this.avatarImageUrl = avatarImageUrl;
    }

    @JsonProperty("cover_image_url")
    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    @JsonProperty("cover_image_url")
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
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
        return new HashCodeBuilder().append(id).append(ownerId).append(name).append(summary).append(description).append(descriptionHtml).append(rules).append(rulesHtml).append(language).append(avatarImageUrl).append(coverImageUrl).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CommunityByName)) {
            return false;
        }
        CommunityByName rhs = (CommunityByName) other;
        return new EqualsBuilder().append(id, rhs.id).append(ownerId, rhs.ownerId).append(name, rhs.name).append(summary, rhs.summary).append(description, rhs.description).append(descriptionHtml, rhs.descriptionHtml).append(rules, rhs.rules).append(rulesHtml, rhs.rulesHtml).append(language, rhs.language).append(avatarImageUrl, rhs.avatarImageUrl).append(coverImageUrl, rhs.coverImageUrl).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}