import React from 'react';
import { Button, Typography, TextField } from '@mui/material';
import { ArrowBack, LibraryAdd } from '@mui/icons-material';

const styleObjectTextFields = () => {
  const style = {
      color: "#000000",
      background: "#81B29A",
      borderRadius: "5px"
  }

  return style;
}

const AddTaskForm = ( { columnID } ) => {
  console.log(`Column to add task to is: ${columnID}`);

  return <div className="add-task-form-container">
            <div id="back-to-project-link-add-task-form">
              <div id="back-to-project-container">
                <ArrowBack id="back-to-project-arrow" />
                <Typography>Back to Project</Typography>
              </div>
            </div>
            <TextField 
              className="add-task-text-field"
              label="Task Title*" 
              color="success"
              variant="filled"
              sx={{ input: styleObjectTextFields}}/>
            <TextField 
              className="add-task-text-field"
              label="Task Description (optional)" 
              color="success"
              variant="filled" 
              multiline
              sx={{
                textarea: styleObjectTextFields,
                "& .MuiFilledInput-root": styleObjectTextFields             
              }}/>
            <Button 
              variant="contained" 
              color="success" 
              size="medium" 
              startIcon={<LibraryAdd />}>Add Task!</Button>
          </div>
}

export default AddTaskForm;
