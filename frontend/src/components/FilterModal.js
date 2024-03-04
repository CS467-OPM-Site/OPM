import React, { useState } from 'react';

function FilterModal({ isOpen, onClose, teams, criteria, setCriteria }) {
    const handleSelectAll = (e) => {
      setCriteria({ all: e.target.checked, teams: {} });
    };
  
    const handleTeamChange = (teamId, checked) => {
      if (checked) {
        const newTeams = { ...criteria.teams, [teamId]: true };
        setCriteria({ all: false, teams: newTeams });
      } else {
        const {[teamId]: omit, ...newTeams} = criteria.teams;
        setCriteria({ all: Object.keys(newTeams).length === 0, teams: newTeams });
      }
    };
  
    return (
      <div style={{ display: isOpen ? 'block' : 'none' }}>
        <button onClick={onClose}>Close</button>
        <div>
          <input
            type="checkbox"
            checked={criteria.all}
            onChange={handleSelectAll}
          /> All
        </div>
        {teams.map(team => (
          <div key={team.teamID}>
            <input
              type="checkbox"
              checked={!!criteria.teams[team.teamID]}
              onChange={(e) => handleTeamChange(team.teamID, e.target.checked)}
            /> {team.teamName}
          </div>
        ))}
      </div>
    );
  };

export default FilterModal;
  