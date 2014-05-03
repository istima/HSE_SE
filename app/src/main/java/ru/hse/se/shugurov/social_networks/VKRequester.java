package ru.hse.se.shugurov.social_networks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.hse.se.shugurov.Requester;

/**
 * Created by Иван on 11.02.14.
 */
public class VKRequester//TODO fix throwing exceptions here, naming conventions
{
    private static final String ACCESS_TOKEN_TAG = "access_token";
    private static final String GROUP_ID_TAG = "group_id";
    private static final String BOARD_GET_COMMENTS = "board.getComments";
    private static final String VK_TOPIC_ID_TAG = "topic_id";
    private static final String REQUEST_BEGINNING = "https://api.vk.com/method/";
    private static final String BOARD_GET_TOPICS = "board.getTopics";
    private static final String WALL_GET_POSTS = "https://api.vk.com/method/wall.get?owner_id=-%s&extended=1";
    private AccessToken accessToken;

    public VKRequester(AccessToken accessToken)
    {
        this.accessToken = accessToken;
    }

    public void getTopics(String groupID, Requester.RequestResultCallback callback)//темы в обсуждении группы
    {
        String request = REQUEST_BEGINNING + BOARD_GET_TOPICS + "?" + GROUP_ID_TAG + "=" + groupID +
                "&" + ACCESS_TOKEN_TAG + "=" + accessToken + "&extended=1&preview=1";
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    //TODO строка с комментами не внизу страницы(
    public VKTopic[] getTopics(String topicsJson)
    {
        if (topicsJson == null)
        {
            return null;
        }
        VKTopic[] vkBoardTopics;
        Map<Integer, VKProfile> profilesMap = new HashMap<Integer, VKProfile>();// key - uid, value - user
        try
        {
            JSONObject jsonObject = new JSONObject(topicsJson);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray itemsJSONArray = responseObject.getJSONArray("topics");
            vkBoardTopics = new VKTopic[itemsJSONArray.length() - 1];
            for (int i = 1; i < itemsJSONArray.length(); i++)
            {
                JSONObject currentTopic = itemsJSONArray.getJSONObject(i);
                int topicID = currentTopic.getInt("tid");
                int authorID = currentTopic.getInt("created_by");
                String text = currentTopic.getString("first_comment");
                int comments = currentTopic.getInt("comments");
                long date = currentTopic.getLong("updated");
                VKProfile user = profilesMap.get(authorID);
                vkBoardTopics[i - 1] = new VKTopic(topicID, user, text, comments, new Date(date * 1000));
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        return vkBoardTopics;
    }

    public void getComments(String groupID, int topicID, Requester.RequestResultCallback callback)
    {
        String request = REQUEST_BEGINNING + BOARD_GET_COMMENTS + "?" + GROUP_ID_TAG + "=" + groupID +
                "&" + VK_TOPIC_ID_TAG + "=" + Integer.toString(topicID) + "&" + ACCESS_TOKEN_TAG + "=" + accessToken + "&extended=1";
        Requester requester = new Requester(callback);
        requester.execute(request);
    }

    public VKAbstractItem[] getComments(String commentsJson)
    {
        if (commentsJson == null)
        {
            return null;
        }
        VKAbstractItem[] comments;
        Map<Integer, VKProfile> profilesMap = new HashMap<Integer, VKProfile>();// key - uid, value - user
        try
        {
            JSONObject jsonObject = new JSONObject(commentsJson);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray jsonComments = responseObject.getJSONArray("comments");
            comments = new VKAbstractItem[jsonComments.length() - 1];
            for (int i = 1; i < jsonComments.length(); i++)
            {
                JSONObject currentComment = jsonComments.getJSONObject(i);
                long date = currentComment.getLong("date") * 1000;
                String text = currentComment.getString("text");
                int authorID = currentComment.getInt("from_id");
                VKProfile profile = profilesMap.get(authorID);
                comments[i - 1] = new VKAbstractItem(profile, text, new Date(date));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
        return comments;
    }

    public void getWallPosts(String groupId, Requester.RequestResultCallback callback)
    {
        Requester requester = new Requester(callback);
        String url = String.format(WALL_GET_POSTS, groupId);
        requester.execute(url);
    }

    public VKTopic[] getWallPosts(String wallPostJson)//TODO обратить мнимание на groups
    {
        VKTopic[] posts = null;
        Map<Integer, VKProfile> profilesMap = new HashMap<Integer, VKProfile>();// key - uid, value - user TODO зачем, если всегда не используется?
        try
        {
            JSONObject jsonObject = new JSONObject(wallPostJson);
            JSONObject responseObject = jsonObject.getJSONObject("response");
            JSONArray profiles = responseObject.getJSONArray("profiles");
            parseProfiles(profilesMap, profiles);
            JSONArray groups = responseObject.getJSONArray("groups");
            parseGroups(profilesMap, groups);
            JSONArray wall = responseObject.getJSONArray("wall");
            posts = new VKTopic[wall.length() - 1];
            for (int i = 1; i < wall.length(); i++)
            {
                JSONObject currentPost = wall.getJSONObject(i);
                int id = currentPost.getInt("id");
                long date = currentPost.getLong("date") * 1000;
                String text = currentPost.getString("text");
                int authorID = currentPost.getInt("from_id");
                VKProfile profile = profilesMap.get(authorID);
                JSONObject commentsObject = currentPost.getJSONObject("comments");
                int commentsQuantity = commentsObject.getInt("count");
                String attachedPicture = null; //TODO почему только одна?(
                if (currentPost.has("attachment"))
                {

                    JSONObject attachedItem = currentPost.getJSONObject("attachment");
                    if (attachedItem.getString("type").equals("photo"))
                    {
                        JSONObject photo = attachedItem.getJSONObject("photo");
                        attachedPicture = photo.getString("src_big");
                    }
                }
                if (attachedPicture == null)
                {
                    posts[i - 1] = new VKTopic(id, profile, text, commentsQuantity, new Date(date));
                } else
                {
                    posts[i - 1] = new VKTopic(id, profile, text, commentsQuantity, new Date(date), attachedPicture);
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return posts;
    }

    private void parseProfiles(Map<Integer, VKProfile> profilesMap, JSONArray profiles)
    {
        for (int i = 0; i < profiles.length(); i++)
        {
            JSONObject currentProfile;
            try
            {
                currentProfile = profiles.getJSONObject(i);
                int userID = currentProfile.getInt("uid");
                String photo = currentProfile.getString("photo_medium_rec");
                String firstName = currentProfile.getString("first_name");
                String lastName = currentProfile.getString("last_name");
                VKProfile currentUser = new VKProfile(userID, firstName + " " + lastName, photo);
                profilesMap.put(userID, currentUser);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void parseGroups(Map<Integer, VKProfile> profilesMap, JSONArray profiles)//TODO тупо копирую код(
    {
        for (int i = 0; i < profiles.length(); i++)
        {
            JSONObject currentGroup;
            try
            {
                currentGroup = profiles.getJSONObject(i);
                int groupID = -currentGroup.getInt("gid");
                String photo = currentGroup.getString("photo_medium");
                String groupName = currentGroup.getString("name");
                VKProfile vkGroup = new VKProfile(groupID, groupName, photo);
                profilesMap.put(groupID, vkGroup);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

}
