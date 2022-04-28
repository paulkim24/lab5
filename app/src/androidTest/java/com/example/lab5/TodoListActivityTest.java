package com.example.lab5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.lab5.TodoDatabase;
import com.example.lab5.TodoListItemDao;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TodoListActivityTest {
    TodoDatabase testDb;
    TodoListItemDao todoListItemDao;

    private static void forceLayout(RecyclerView recyclerView){
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0, 0, 1080, 2280);
    }

    @Before
    public void resetDatabase(){
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, TodoDatabase.class)
                .allowMainThreadQueries()
                .build();
        TodoDatabase.injectTestDatabase(testDb);

        List<TodoListItem> todos = TodoListItem.loadJSON(context, "demo_todos.json");
        todoListItemDao = testDb.todoListItemDao();
        todoListItemDao.insertAll(todos);
    }
    @Test
    public void testEditTodoText(){
        String newText = "Ensure all tests pass";
        ActivityScenario<TodoListActivity> scenario
                = ActivityScenario.launch(TodoListActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
           RecyclerView recyclerView = activity.recyclerView;
           RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
           assertNotNull(firstVH);
           long id = firstVH.getItemId();

           EditText todoText = firstVH.itemView.findViewById(R.id.todo_item_text);
           todoText.requestFocus();
           todoText.setText("Ensure all tests pass");
           todoText.clearFocus();

           TodoListItem editedItem = todoListItemDao.get(id);
           assertEquals(newText, editedItem.text);
        });
    }


    @Test
    public void TestAddNewTodo() {
        String newText = "Ensure all tests pass";

        ActivityScenario<TodoListActivity> scenario
                = ActivityScenario.launch(TodoListActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            List<TodoListItem> beforeTodoList = todoListItemDao.getAll();

            EditText newTodoText = activity.findViewById(R.id.new_todo_text);
            Button addTodoButton = activity.findViewById(R.id.add_todo_btn);

            newTodoText.setText(newText);
            addTodoButton.performClick();

            List<TodoListItem> afterTodoList = todoListItemDao.getAll();
            assertEquals(beforeTodoList.size() + 1, afterTodoList.size());
            assertEquals(newText, afterTodoList.get(afterTodoList.size() - 1).text);

        });
    }
}
