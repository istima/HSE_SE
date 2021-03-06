package ru.hse.shugurov.gui;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.hse.shugurov.social_networks.AbstractRequester;
import ru.hse.shugurov.social_networks.SocialNetworkTopic;
import ru.hse.shugurov.social_networks.StateListener;
import ru.hse.shugurov.social_networks.VkWallPostsAdapter;

/**
 * Used to demonstrate vk wall posts.
 * *
 * For the required arguments see{@link ru.hse.shugurov.gui.SocialNetworkAbstractList}
 * <p/>
 *
 * @author Ivan Shugurov
 */
public class WallPostScreen extends SocialNetworkAbstractList
{
    /*constants used as keys in bundle object*/
    private static final String POSTS_ID = "vk_posts_array";
    private static final String COMMENTS_STATE_TAG = "comments_state_tag";


    private SocialNetworkTopic[] posts;
    private AtomicBoolean commentsChanged = new AtomicBoolean(false);


    /*sts adapter to a list view and adds  OnClickListener*/
    private void fillList()
    {
        commentsChanged.set(false);
        if (isAdded())
        {
            setListAdapter(new VkWallPostsAdapter(getActivity(), posts));
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    StateListener stateListener = new StateListener(commentsChanged);
                    WallCommentsFragment wallCommentsFragment = new WallCommentsFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString(SocialNetworkAbstractList.GROUP_ID_TAG, getGroupId());
                    arguments.putString(SocialNetworkAbstractList.GROUP_NAME_TAG, getGroupName());
                    arguments.putParcelable(WallCommentsFragment.WALL_COMMENTS_POST_TAG, posts[position]);
                    arguments.putSerializable(SocialNetworkAbstractList.REQUESTER_TAG, getRequester());
                    arguments.putSerializable(CommentsFragment.COMMENTS_LISTENER_TAG, stateListener);
                    wallCommentsFragment.setArguments(arguments);
                    ScreenFactory.changeFragments(getFragmentManager(), wallCommentsFragment);
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(POSTS_ID);
            if (parcelables == null)
            {
                posts = new SocialNetworkTopic[0];
            } else
            {
                posts = Arrays.copyOf(parcelables, parcelables.length, SocialNetworkTopic[].class);
            }
            commentsChanged = (AtomicBoolean) savedInstanceState.getSerializable(COMMENTS_STATE_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(POSTS_ID, posts);
        outState.putSerializable(COMMENTS_STATE_TAG, commentsChanged);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (posts == null || commentsChanged.get())
        {
            getRequester().getWallPosts(getGroupId(), new AbstractRequester.RequestResultListener<SocialNetworkTopic>()
            {
                @Override
                public void resultObtained(SocialNetworkTopic[] resultPosts)
                {
                    if (resultPosts == null)
                    {
                        Toast.makeText(getActivity(), "Не удалось загрузить информацию", Toast.LENGTH_SHORT).show();
                    } else
                    {
                        posts = resultPosts;
                        fillList();
                    }
                }
            });
        } else
        {
            fillList();
        }
    }
}
