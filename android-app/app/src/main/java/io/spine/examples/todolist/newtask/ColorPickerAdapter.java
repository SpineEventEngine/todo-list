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

import android.arch.lifecycle.MutableLiveData;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.common.collect.ImmutableList;
import io.spine.examples.todolist.LabelColor;
import io.spine.examples.todolist.R;
import io.spine.examples.todolist.lifecycle.Consumer;

import java.util.List;

import static io.spine.examples.todolist.model.Colors.toRgb;

public class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewBinder> {

    private static final List<LabelColor> COLORS;

    static {
        final ImmutableList.Builder<LabelColor> colorsBuilder = ImmutableList.builder();
        final LabelColor[] declaredColors = LabelColor.values();
        for (LabelColor color : declaredColors) {
            if (color != LabelColor.UNRECOGNIZED && color.getNumber() > 0) {
                colorsBuilder.add(color);
            }
        }
        COLORS = colorsBuilder.build();
    }

    private final MutableLiveData<LabelColor> selected = new MutableLiveData<>();

    @Override
    public ViewBinder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View element = LayoutInflater.from(parent.getContext())
                                           .inflate(R.layout.item_color_option, parent, false);
        return new ViewBinder(element, selected);
    }

    @Override
    public void onBindViewHolder(ViewBinder holder, int position) {
        final LabelColor color = COLORS.get(position);
        holder.bind(color);
    }

    @Override
    public int getItemCount() {
        return COLORS.size();
    }

    void observeColor(Consumer<LabelColor> callback) {
        selected.observeForever(callback::accept);
    }

    static class ViewBinder extends RecyclerView.ViewHolder {

        private final MutableLiveData<LabelColor> selector;
        private final ImageView colorView;

        private ViewBinder(View itemView, MutableLiveData<LabelColor> selector) {
            super(itemView);
            this.selector = selector;
            this.colorView = itemView.findViewById(R.id.color);
        }

        private void bind(LabelColor color) {
            final int rgb = toRgb(color);
            colorView.setColorFilter(rgb);
            colorView.setOnClickListener(view -> selector.setValue(color));
        }
    }
}