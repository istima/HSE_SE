package ru.hse.se.shugurov.gui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.Requester;
import ru.hse.se.shugurov.social_networks.AbstractRequester;
import ru.hse.se.shugurov.social_networks.SocialNetworkCommentsAdapter;
import ru.hse.se.shugurov.social_networks.SocialNetworkEntry;
import ru.hse.se.shugurov.social_networks.SocialNetworkProfile;
import ru.hse.se.shugurov.social_networks.SocialNetworkTopic;
import ru.hse.se.shugurov.social_networks.VKRequester;
import ru.hse.se.shugurov.utills.ImageLoader;

/**
 * Created by Иван on 03.05.2014.
 */
public class WallCommentsScreen extends SocialNetworkAbstractList
{
    private final String VK_WALL_COMMENTS_TAG = "vk_wall_comments";
    private final String VK_WALL_COMMENTS_POST_TAG = "vk_wall_comments_post";
    private final String TYPED_COMMENT = "vk_wall_typed_comment";
    private SocialNetworkTopic post;
    private SocialNetworkEntry[] comments;
    private int containerWidth;
    private String commentText;
    private EditText input;
    private View headerView;
    private View footerView;

    public WallCommentsScreen()
    {

    }

    public WallCommentsScreen(String groupId, String groupName, SocialNetworkTopic post, AbstractRequester requester)
    {
        super(groupId, groupName, requester);
        this.post = post;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && post == null)
        {
            post = savedInstanceState.getParcelable(VK_WALL_COMMENTS_POST_TAG);
            comments = (SocialNetworkEntry[]) savedInstanceState.getParcelableArray(VK_WALL_COMMENTS_TAG);
            commentText = savedInstanceState.getString(TYPED_COMMENT);
        }
    }

    @TargetApi(13)
    private int getScreenSizeAfterAPI13(Display display)
    {
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @SuppressWarnings("deprecation")
    private int getScreenSizeBeforeAPI13(Display display)
    {
        return display.getWidth();
    }

    private int getScreenWidth()
    {
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13)
        {
            return getScreenSizeAfterAPI13(display);
        } else
        {
            return getScreenSizeBeforeAPI13(display);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View resultView = super.onCreateView(inflater, container, savedInstanceState);
        containerWidth = container.getWidth() - container.getPaddingLeft() - container.getPaddingRight();
        if (containerWidth == 0)
        {
            containerWidth = getScreenWidth() - container.getPaddingLeft() - container.getPaddingRight();
        }
        return resultView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (comments == null)
        {
            loadComments();
        } else
        {
            boolean emptyProfilesOccur = false;
            for (int i = 0; i < comments.length; i++)
            {
                if (comments[i].getAuthor().getFullName() == null)
                {
                    VKRequester.getProfileInformation(comments, new Requester.RequestResultCallback()
                    {
                        @Override
                        public void pushResult(String result)
                        {
                            handleFullProfilesInformationObtaining(result);
                        }
                    });
                    emptyProfilesOccur = true;
                    break;
                }
            }
            if (!emptyProfilesOccur)
            {
                instantiateAdapter();
            }
        }
        getListView().setSelector(new StateListDrawable());
    }

    private void loadComments()
    {
        getRequester().getWallComments(getGroupId(), post.getId(), new Requester.RequestResultCallback()
        {
            @Override
            public void pushResult(String result)
            {
                if (result == null)
                {
                    Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
                } else
                {
                    comments = VKRequester.getWallComments(result);
                    VKRequester.getProfileInformation(comments, new Requester.RequestResultCallback()
                    {
                        @Override
                        public void pushResult(String result)
                        {
                            handleFullProfilesInformationObtaining(result);
                        }
                    });
                }
            }
        });
    }

    private void handleFullProfilesInformationObtaining(String result)
    {
        if (result == null)
        {
            Toast.makeText(getActivity(), "Нет Интернет соединения", Toast.LENGTH_SHORT).show();
        } else
        {
            VKRequester.fillProfileInformation(comments, result);
            instantiateAdapter();
        }
    }

    private void instantiateAdapter()
    {
        SocialNetworkCommentsAdapter responsesAdapter = new SocialNetworkCommentsAdapter(getActivity(), comments);
        if (headerView == null)
        {
            createHeaderView();
            getListView().addHeaderView(headerView);
        }
        if (footerView == null)
        {
            createFooterView();
            getListView().addFooterView(footerView);
        }
        setListAdapter(responsesAdapter);
        setListShown(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(VK_WALL_COMMENTS_TAG, comments);
        outState.putParcelable(VK_WALL_COMMENTS_POST_TAG, post);
        outState.putString(TYPED_COMMENT, input.getText().toString());
    }


    private void createHeaderView()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        headerView = inflater.inflate(R.layout.vk_wall_post, null, false);
        ImageLoader imageLoader = ImageLoader.instance();
        SocialNetworkProfile author = post.getAuthor();
        ImageView authorPhoto = (ImageView) headerView.findViewById(R.id.vk_post_author_photo);
        authorPhoto.setImageBitmap(null);
        float weightSum = ((LinearLayout) headerView).getWeightSum();
        int photoWidth = (int) (containerWidth * (1 / weightSum));
        FlexibleImageView authorPhotoProxy = new FlexibleImageView(authorPhoto, photoWidth);
        imageLoader.displayImage(author.getPhoto(), authorPhotoProxy);
        ImageView attachedPicture = (ImageView) headerView.findViewById(R.id.vk_wall_attached_picture);
        attachedPicture.setImageBitmap(null);
        if (post.getAttachedPicture() != null)
        {
            attachedPicture.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            photoWidth = containerWidth - photoWidth;
            FlexibleImageView attachedImageProxy = new FlexibleImageView(attachedPicture, photoWidth);
            imageLoader.displayImage(post.getAttachedPicture(), attachedImageProxy);
        } else
        {
            attachedPicture.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        ((TextView) headerView.findViewById(R.id.vk_wall_post_author_name)).setText(author.getFullName());
        ((TextView) headerView.findViewById(R.id.vk_wall_post_text)).setText(Html.fromHtml(post.getText()));
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);
        ((TextView) headerView.findViewById(R.id.vk_date)).setText(format.format(post.getDate()));
    }

    private void createFooterView()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        footerView = inflater.inflate(R.layout.send_form, null, false);
        input = (EditText) footerView.findViewById(R.id.send_form_text);
        input.setText(commentText);
        Button sendButton = (Button) footerView.findViewById(R.id.send_form_button);
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                commentText = input.getText().toString();
                if (commentText.length() == 0)
                {
                    Toast.makeText(getActivity(), "Нельзя добавлять пустой комментарий", Toast.LENGTH_SHORT).show();
                } else
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    setListShown(false);
                    Toast.makeText(getActivity(), "Отправка комментария", Toast.LENGTH_SHORT).show();
                    getRequester().addCommentToWallPost(getGroupId(), post.getId(), commentText, new Requester.RequestResultCallback()
                    {
                        @Override
                        public void pushResult(String result)
                        {
                            if (result == null || (result != null && result.contains("error")))
                            {
                                Toast.makeText(getActivity(), "Не удалось добавить комментарий", Toast.LENGTH_SHORT).show();
                                setListShown(true);
                            } else
                            {
                                commentText = null;
                                input.setText("");
                                loadComments();
                            }
                        }
                    });

                }
            }
        });
    }

}
