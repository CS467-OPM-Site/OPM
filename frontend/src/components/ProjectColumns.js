import React, { memo } from 'react';
import { Typography } from '@mui/material';

const ProjectColumn = memo(( { columnTitle, columnID} ) => {

  return <div className="column-card" key={columnID}>
            <Typography variant="h5" component="div" className="column-title {columnTitle}">
              {columnTitle}
            </Typography>
          </div>
});

export default ProjectColumn;
