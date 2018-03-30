package shine.app.tubeviewmultitask;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import shine.tran.localtubeview.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoPlayerFragment extends Fragment {
    @BindView(R.id.video_layout)
    RelativeLayout videoLayout;
    @BindView(R.id.list_layout)
    RelativeLayout listLayout;
    @BindView(R.id.draggable_view)
    DraggableView mDraggableView;
    Unbinder unbinder;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    public static VideoPlayerFragment newInstance() {
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        unbinder = ButterKnife.bind(this, view);
        hookDraggableViewListener();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    private void hookDraggableViewListener() {
        mDraggableView.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {

            }

            //Empty
            @Override
            public void onMinimized() {

            }

            @Override
            public void onClosedToLeft() {
                ((MainActivity)getActivity()).stopService();
                WebPlayer.getPlayer().destroy();
            }

            @Override
            public void onClosedToRight() {
                ((MainActivity)getActivity()).stopService();
                WebPlayer.getPlayer().destroy();
            }
        });
    }
}
