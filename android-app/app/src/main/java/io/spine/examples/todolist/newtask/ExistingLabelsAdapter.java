/*
 * Copyright 2017, TeamDev Ltd. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.examples.todolist.newtask;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import io.spine.examples.todolist.LabelId;
import io.spine.examples.todolist.R;
import io.spine.examples.todolist.TaskLabel;
import io.spine.examples.todolist.model.Colors;
import io.spine.examples.todolist.q.projection.LabelledTasksView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static io.spine.examples.todolist.model.Colors.toRgb;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableCollection;

final class ExistingLabelsAdapter extends RecyclerView.Adapter<ExistingLabelsAdapter.ViewBinder> {

    private static final String TAG = ExistingLabelsAdapter.class.getSimpleName();

    private final Future<List<LabelledTasksView>> data;
    private final Set<LabelId> selectedLabels = newHashSet();

    ExistingLabelsAdapter(Future<List<LabelledTasksView>> data) {
        this.data = data;
    }

    @Override
    public ViewBinder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View root = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.item_label, parent, false);
        return new ViewBinder(root, selectedLabels);
    }

    @Override
    public void onBindViewHolder(ViewBinder holder, int position) {
        final List<LabelledTasksView> elements = joinData();
        final LabelledTasksView element = elements.get(position);
        holder.bind(element);
    }

    @Override
    public int getItemCount() {
        final List<LabelledTasksView> elements = joinData();
        return elements.size();
    }

    Collection<LabelId> getSelected() {
        return unmodifiableCollection(selectedLabels);
    }

    private List<LabelledTasksView> joinData() {
        try {
            return data.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Failed to fetch labels", e);
            return emptyList();
        }
    }

    static class ViewBinder extends RecyclerView.ViewHolder {

        private final Set<LabelId> selectedLabels;

        private final TextView titleLabel;
        private final View colorView;

        private final View root;

        private LabelId labelId;

        private ViewBinder(View itemView, Set<LabelId> selectedLabels) {
            super(itemView);
            this.selectedLabels = selectedLabels;

            this.titleLabel = itemView.findViewById(R.id.label_title);
            this.colorView = itemView.findViewById(R.id.label_color_stripe);
            this.root = itemView;
        }

        private void bind(LabelledTasksView label) {
            this.labelId = label.getLabelId();

            final String text = label.getLabelTitle();
            final int colorRgb = Colors.toRgb(label);
            titleLabel.setText(text);
            colorView.setBackgroundColor(colorRgb);

            root.setOnClickListener(unselectedListener());
        }

        private OnClickListener unselectedListener() {
            return view -> {
                view.setOnClickListener(selectedListener());
                view.setSelected(true);
                selectedLabels.add(labelId);
            };
        }

        private OnClickListener selectedListener() {
            return view -> {
                view.setOnClickListener(unselectedListener());
                view.setSelected(false);
                selectedLabels.remove(labelId);
            };
        }
    }
}
