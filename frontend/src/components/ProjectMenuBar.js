import React, { useState } from 'react';
import { Button, Typography, TextField } from '@mui/material';
import { Cancel } from '@mui/icons-material';

const MAKE_NEW_COLUMN = "Make a New Column";
const ADD_COLUMN = "Add Column!";

const ProjectMenuBar = ({ projectName }) => {
  const [isAddColumnFieldShown, setIsAddColumnFieldShown] = useState(false);
  const [isAddColumnButtonEnabled, setIsAddColumnButtonEnabled] = useState(true);
  const [addColumnButtonText, setAddColumnButtonText] = useState(MAKE_NEW_COLUMN);

  const handleOnAddColumnClick = () => {
    if (!isAddColumnFieldShown) {
      setIsAddColumnFieldShown(true);
      setIsAddColumnButtonEnabled(false);
      setAddColumnButtonText(ADD_COLUMN);
    } else {
      setIsAddColumnFieldShown(!isAddColumnFieldShown);
    }
  }

  const onColumnTitleInputChanged = (e) => {
    if (e.target.value.length === 3) {
      setIsAddColumnButtonEnabled(true);
    } else if (e.target.value.length < 3) {
      setIsAddColumnButtonEnabled(false);
    }
  }

  const onCloseAddColumnPressed = () => {
    setIsAddColumnFieldShown(false);
    setAddColumnButtonText(MAKE_NEW_COLUMN);
    setIsAddColumnButtonEnabled(true);
  }

  return <div style={{ width: 'inherit', 'height': '8%', 'min-height': '75px', 'overflow': 'hidden' }}>
            <div className="project-page-menu-bar">
              <Button variant="contained" color="success">Back to Projects</Button>
              <Typography variant="h4" component="h1" className="project-page-project-title">
                {projectName}
              </Typography>
              <div className="project-page-add-column-container">
                <Button variant="contained" color="success" onClick={handleOnAddColumnClick} disabled={!isAddColumnButtonEnabled}>{addColumnButtonText}</Button>
                {isAddColumnFieldShown && 
                  <>
                  <TextField 
                    autoFocus
                    className="addColumnTitleElem"
                    id="columnTitle"
                    variant="filled" 
                    size="small" 
                    label="Column Title*"
                    sx={{
                      input: {
                        color: "#000000",
                        'background': "#81B29A",
                        'border-radius': '5px'
                      },
                      fieldset: {
                        'color': '#000000',
                        'border-radius': '5px'
                      },
                      label: {
                        color: "#000000"
                      },
                      "& .MuiFilledInput-root::after": {
                        'border-color': "#2E7D32"
                      }
                    }} 
                    InputLabelProps={{ style: {color: "black" } }}
                    onChange={onColumnTitleInputChanged}
                    >
                  </TextField>
                  <Cancel className="icon" onClick={onCloseAddColumnPressed} sx={{ color: "#FF1D8E" }}></Cancel>
                  </>
                }
              </div>
            </div>
          </div>
}

export default ProjectMenuBar;

