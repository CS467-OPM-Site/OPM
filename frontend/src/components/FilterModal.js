import React from 'react';
import '../styles/FilterModal.css';

function FilterModal({ isOpen, onClose, teams, criteria, setCriteria }) {
  const handleSelectAll = (e) => {
    setCriteria({ all: e.target.checked, teams: {} });
  };

  const handleTeamChange = (teamId, checked) => {
    if (checked) {
      const newTeams = { ...criteria.teams, [teamId]: true };
      setCriteria({ all: false, teams: newTeams });
    } else {
      const { [teamId]: omit, ...newTeams } = criteria.teams;
      setCriteria({ all: Object.keys(newTeams).length === 0, teams: newTeams });
    }
  };

  return (
    <div className={`modal-backdrop ${isOpen ? '' : 'hidden'}`}>
      <div className="filter-modal">
        <div className="filter-modal-header">
          <h2 className="filter-modal-title">Filter Projects by Team Selection</h2>
          <button onClick={onClose} className="filter-modal-close-btn">X</button>
        </div>
        <div className="filter-modal-content">
          <div className="filter-modal-checkbox">
            <label>
              <input
                type="checkbox"
                checked={criteria.all}
                onChange={handleSelectAll}
              />
              All
            </label>
          </div>
          {teams.map(team => (
            <div className="filter-modal-checkbox" key={team.teamID}>
              <label>
                <input
                  type="checkbox"
                  checked={!!criteria.teams[team.teamID]}
                  onChange={(e) => handleTeamChange(team.teamID, e.target.checked)}
                />
                {team.teamName}
              </label>
            </div>
          ))}
        </div>
        <div className="filter-modal-actions">
          <button onClick={onClose} className="filter-modal-button filter-modal-button-secondary">Close</button>
        </div>
      </div>
    </div>
  );
}

export default FilterModal;
